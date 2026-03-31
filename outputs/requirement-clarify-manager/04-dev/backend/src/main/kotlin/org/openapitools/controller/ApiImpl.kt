// File: src/main/kotlin/org/openapitools/controller/ApiImpl.kt
package org.openapitools.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.openapitools.api.ApiApiDelegate
import org.openapitools.model.*
import org.openapitools.service.RequirementService
import org.openapitools.service.DataDictionaryService
import org.openapitools.service.AnalysisService
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * 커스텀 구현체.
 */
@Service
class ApiImpl(
    private val requirementService: RequirementService,
    private val dataDictionaryService: DataDictionaryService,
    private val analysisService: AnalysisService,
    private val objectMapper: ObjectMapper
) : ApiApiDelegate {

    // ── 요구사항 관리 (TC-001 ~ TC-019) ─────────────────────────────────

    override fun apiV1RequirementsPost(
        `data`: String,
        file: Resource?
    ): ResponseEntity<RequirementResponse> {
        val fileBytes = file?.inputStream?.readBytes()
        val result = requirementService.createRequirement(`data`, fileBytes)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    override fun apiV1RequirementsIdStatusPatch(
        id: Int,
        apiV1RequirementsIdStatusPatchRequest: ApiV1RequirementsIdStatusPatchRequest
    ): ResponseEntity<ApiV1RequirementsIdStatusPatch200Response> {
        val saved = requirementService.updateStatus(id, apiV1RequirementsIdStatusPatchRequest.status.value)
        
        return ResponseEntity.ok(ApiV1RequirementsIdStatusPatch200Response(
            id = saved.id,
            status = saved.status.value,
            updatedAt = saved.updatedAt
        ))
    }

    // ── 데이터 사전 (TC-020 ~ TC-027) ── Phase 3 구현 ──────────────────────

    override fun apiV1RequirementsIdDataDictionaryExtractPost(id: Int): ResponseEntity<AsyncJobResponse> {
        val result = dataDictionaryService.extractTerms(id)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(AsyncJobResponse(
            jobId = "EXTRACT-$id",
            status = "Processing",
            message = result
        ))
    }

    override fun apiV1ProjectsProjectNameDataDictionaryPendingGet(projectName: String): ResponseEntity<PendingTermResponse> {
        val result = dataDictionaryService.getNextPendingTerm(projectName)
        return ResponseEntity.ok(result)
    }

    override fun apiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost(
        projectName: String,
        termId: Int,
        clarifyRequest: ClarifyRequest
    ): ResponseEntity<ApiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost200Response> {
        dataDictionaryService.clarifyTerm(projectName, termId, clarifyRequest)
        val next = dataDictionaryService.getNextPendingTerm(projectName)
        return ResponseEntity.ok(ApiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost200Response(
            updated = true,
            nextPendingTerm = next
        ))
    }

    // ── Event Storming (TC-028 ~ TC-036) ── Phase 4 & 5 ──────────────────

    override fun apiV1RequirementsIdAnalyzePost(id: Int): ResponseEntity<AsyncJobResponse> {
        val result = try {
            analysisService.triggerAnalysis(id)
        } catch (e: IllegalStateException) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(AsyncJobResponse(
                jobId = "ANALYZE-$id",
                status = "Rejected",
                message = e.message ?: "Analysis preconditions not met."
            ))
        }
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(AsyncJobResponse(
            jobId = "ANALYZE-$id",
            status = "Accepted",
            message = result
        ))
    }

    override fun apiV1RequirementsIdAnalysisViewGet(id: Int): ResponseEntity<AnalysisViewResponse> {
        val result = analysisService.getAnalysisResult(id)
        
        return if (result != null && result.status == org.openapitools.entity.AnalysisStatus.COMPLETED) {
            // 중첩된 OpenAPI 모델 구조에 맞춰 매핑 (TC-032)
            ResponseEntity.ok(AnalysisViewResponse(
                requirement = null, // TODO: Requirement 상세 정보 연동 필요 시 추가
                eventStorming = AnalysisViewResponseEventStorming(
                    actors = objectMapper.readValue(result.actors),
                    commands = objectMapper.readValue(result.commands),
                    domainEvents = objectMapper.readValue(result.domainEvents),
                    aggregates = objectMapper.readValue(result.aggregates),
                    externalSystems = objectMapper.readValue(result.externalSystems)
                ),
                diagrams = AnalysisViewResponseDiagrams(
                    businessProcess = result.diagramSource
                )
            ))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}
