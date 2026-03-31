// File: src/main/kotlin/org/openapitools/service/DataDictionaryService.kt
package org.openapitools.service

import org.openapitools.entity.*
import org.openapitools.repository.*
import org.openapitools.model.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import org.springframework.scheduling.annotation.Async

/**
 * 데이터 사전 명확화 관리 서비스.
 */
@Service
class DataDictionaryService(
    private val requirementRepository: RequirementRepository,
    private val dictionaryRepository: DataDictionaryRepository
) {

    @Transactional
    fun extractTerms(requirementId: Int): String {
        val req = requirementRepository.findById(requirementId).orElseThrow {
            IllegalArgumentException("Requirement with id $requirementId not found")
        }

        // 간단한 텍스트 추출 로직 (MVP: 한글 명사구 위주로 가정)
        // 실제 운영에선 NLP 라이브러리 연거 필요
        val text = req.originalContent
        val tokens = extractNounsFromText(text)

        tokens.forEach { koreanName ->
            if (!dictionaryRepository.existsByRequirementIdAndKoreanName(requirementId, koreanName)) {
                dictionaryRepository.save(DataDictionaryEntity(
                    requirementId = requirementId,
                    projectName = req.projectName,
                    koreanName = koreanName,
                    status = TermStatus.PENDING
                ))
            }
        }

        return "Extract job started for requirement id: $requirementId"
    }

    private fun extractNounsFromText(text: String): Set<String> {
        // 간단한 한글 명사 추출 정규식 (2~10자 한글)
        // 실제 운영 시 더 복잡한 NLP 엔진 연동 필요
        val regex = Regex("[가-힣]{2,10}")
        val results = regex.findAll(text).map { it.value }.toMutableSet()
        
        // 특정 키워드 추가
        if (text.contains("ID")) results.add("ID")
        if (text.contains("Jira")) results.add("Jira")
        
        return results
    }

    @Transactional(readOnly = true)
    fun getNextPendingTerm(projectName: String): PendingTermResponse {
        val term = dictionaryRepository.findFirstByProjectNameAndStatusOrderByCreatedAtAsc(
            projectName, TermStatus.PENDING
        )
        
        val remainingCount = dictionaryRepository.countByProjectNameAndStatus(projectName, TermStatus.PENDING)

        return if (term.isPresent) {
            val t = term.get()
            PendingTermResponse(
                termId = t.id,
                koreanName = t.koreanName,
                isComplete = false,
                remainingCount = remainingCount.toInt()
            )
        } else {
            PendingTermResponse(
                termId = 0,
                koreanName = "",
                isComplete = true,
                remainingCount = 0
            )
        }
    }

    @Transactional
    fun clarifyTerm(projectName: String, termId: Int, request: ClarifyRequest): DataDictionaryEntity {
        val term = dictionaryRepository.findById(termId).orElseThrow {
            IllegalArgumentException("Term ID $termId not found")
        }

        // TC-021: 프로젝트간 격리 검증 (Project Name Match)
        if (term.projectName != projectName) {
            throw IllegalArgumentException("Project name mismatch for term $termId")
        }

        // 상태 업데이트
        if (request.action == ClarifyRequest.Action.DEFINE) {
            term.englishName = request.englishName
            term.dataType = request.dataType
            term.dataLength = request.dataLength
            term.description = request.description
            term.status = TermStatus.DEFINED
        } else if (request.action == ClarifyRequest.Action.SKIP) {
            term.status = TermStatus.SKIPPED
        }

        term.updatedAt = OffsetDateTime.now()
        // JPA @Version 이 Optimistic Lock (TC-026) 처리

        return dictionaryRepository.save(term)
    }
}
