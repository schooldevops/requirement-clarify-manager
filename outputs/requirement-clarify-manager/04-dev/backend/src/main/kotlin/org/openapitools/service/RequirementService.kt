// File: src/main/kotlin/org/openapitools/service/RequirementService.kt
package org.openapitools.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.openapitools.entity.RequirementEntity
import org.openapitools.entity.RequirementHistory
import org.openapitools.entity.RequirementStatus
import org.openapitools.exception.DuplicateRequirementException
import org.openapitools.model.RequirementResponse
import org.openapitools.repository.RequirementHistoryRepository
import org.openapitools.repository.RequirementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

/**
 * 요구사항 관리 서비스.
 */
@Service
class RequirementService(
    private val requirementRepository: RequirementRepository,
    private val historyRepository: RequirementHistoryRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun createRequirement(data: String, fileBytes: ByteArray?): RequirementResponse {
        val reqData = try {
            objectMapper.readValue<Map<String, Any>>(data)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON data format")
        }

        val projectName = reqData["projectName"] as? String
            ?: throw IllegalArgumentException("ERR-VAL-001: projectName is required")
        val requirementNo = reqData["requirementNo"] as? String
            ?: throw IllegalArgumentException("ERR-VAL-001: requirementNo is required")
        val version = reqData["version"] as? String
            ?: throw IllegalArgumentException("ERR-VAL-001: version is required")

        // TC-004,005: Version format validation (M.m.p)
        if (!version.matches(Regex("^\\d+\\.\\d+\\.\\d+\$"))) {
            throw IllegalArgumentException("ERR-VAL-007: Invalid version format. Expected M.m.p (e.g. 1.0.0)")
        }

        // TC-007: Duplicate JIRA ID check
        if (requirementRepository.existsByRequirementNo(requirementNo)) {
            throw DuplicateRequirementException("ERR-DUP-001: Requirement ID '$requirementNo' already exists")
        }

        val entity = RequirementEntity(
            projectName = projectName,
            requirementNo = requirementNo,
            version = version,
            status = RequirementStatus.Draft,
            originalContent = fileBytes?.toString(Charsets.UTF_8) ?: ""
        )

        val saved = requirementRepository.save(entity)

        return RequirementResponse(
            id = saved.id,
            requirementNo = saved.requirementNo,
            status = saved.status.value, // value 사용
            createdAt = OffsetDateTime.now()
        )
    }

    @Transactional
    fun updateStatus(id: Int, requestedStatusValue: String): RequirementEntity {
        val req = requirementRepository.findById(id).orElseThrow {
            IllegalArgumentException("Requirement not found with id: $id")
        }

        val newStatus = try {
            RequirementStatus.fromValue(requestedStatusValue)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid status: $requestedStatusValue")
        }

        // TC-019: Done 상태인 완료건은 수정 불가
        if (req.status == RequirementStatus.Done) {
            throw IllegalStateException("ERR-STS-002: Cannot modify requirements in 'Done' status")
        }

        // TC-013 ~ TC-018: 상태 전이 규칙 검증
        if (!isValidTransition(req.status, newStatus)) {
            throw InvalidStatusTransitionException("ERR-STS-001: Invalid status transition from ${req.status.value} to ${newStatus.value}")
        }

        val fromStatus = req.status
        req.status = newStatus
        req.updatedAt = OffsetDateTime.now()

        val saved = requirementRepository.save(req)

        // 이력 기록
        historyRepository.save(RequirementHistory(
            requirementId = saved.id,
            fromStatus = fromStatus,
            toStatus = newStatus
        ))

        return saved
    }

    private fun isValidTransition(from: RequirementStatus, to: RequirementStatus): Boolean {
        if (from == to) return true

        return when (from) {
            RequirementStatus.Draft -> to == RequirementStatus.Clarifying
            RequirementStatus.Clarifying -> to == RequirementStatus.Clarified
            RequirementStatus.Clarified -> to == RequirementStatus.InProgress
            RequirementStatus.InProgress -> to == RequirementStatus.Done
            RequirementStatus.Done -> false
        }
    }
}

class InvalidStatusTransitionException(message: String) : RuntimeException(message)
