// File: src/main/kotlin/org/openapitools/service/AnalysisService.kt
package org.openapitools.service

import org.openapitools.entity.*
import org.openapitools.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Event Storming 및 요구사항 정밀 분석 서비스.
 */
@Service
class AnalysisService(
    private val requirementRepository: RequirementRepository,
    private val dictionaryRepository: DataDictionaryRepository,
    private val resultRepository: AnalysisResultRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun triggerAnalysis(requirementId: Int): String {
        val req = requirementRepository.findById(requirementId).orElseThrow {
            IllegalArgumentException("Requirement $requirementId not found")
        }

        // TC-029: 선행조건 검증 (미정의 용어 잔존 여부)
        val pendingCount = dictionaryRepository.countByProjectNameAndStatus(req.projectName, TermStatus.PENDING)
        if (pendingCount > 0) {
            throw IllegalStateException("ERR-FLOW-001: Cannot start analysis. $pendingCount pending terms remain in the data dictionary.")
        }

        // 비동기 작업 시뮬레이션
        runAsyncAnalysis(requirementId)

        return "Analysis job started for requirement: ${req.requirementNo}"
    }

    @Async
    fun runAsyncAnalysis(requirementId: Int) {
        // 1. 초기 상태 저장
        resultRepository.save(AnalysisResultEntity(
            requirementId = requirementId,
            status = AnalysisStatus.PROCESSING
        ))

        Thread.sleep(2000) // 분석 시뮬레이션 (TC-031)

        val req = requirementRepository.findById(requirementId).get()
        val dictionary = dictionaryRepository.findAllByRequirementId(requirementId)
        
        // 2. 간단한 분석 로직 (TC-030)
        // 용어 사전의 English Name 을 Actor/Aggregate 로 매핑
        val actors = dictionary.filter { it.dataType == "Actor" || it.englishName?.contains("User") == true }
            .map { it.englishName ?: it.koreanName }
        val domainEvents = dictionary.map { "${it.koreanName}처리됨" }
        val commands = dictionary.map { "${it.koreanName}처리" }

        val result = AnalysisResultEntity(
            requirementId = requirementId,
            actors = objectMapper.writeValueAsString(actors),
            commands = objectMapper.writeValueAsString(commands),
            domainEvents = objectMapper.writeValueAsString(domainEvents),
            aggregates = objectMapper.writeValueAsString(listOf(req.projectName)),
            diagramSource = "graph LR\n  A[Actor] --> C[Command] --> E[Event]",
            status = AnalysisStatus.COMPLETED
        )

        resultRepository.save(result)
    }

    @Transactional(readOnly = true)
    fun getAnalysisResult(requirementId: Int): AnalysisResultEntity? {
        return resultRepository.findByRequirementId(requirementId).orElse(null)
    }
}
