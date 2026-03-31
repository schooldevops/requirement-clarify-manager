package org.openapitools.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.openapitools.model.RequirementResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.OffsetDateTime

@Service
class RequirementService(
    private val requirementRepository: RequirementRepository
) {
    private val objectMapper = ObjectMapper()

    fun createRequirement(data: String, file: MultipartFile?): RequirementResponse {
        val rootNode = objectMapper.readTree(data)
        
        val requirementNo = rootNode.path("requirementNo").asText(null) 
            ?: throw IllegalArgumentException("ERR-VAL-001: 필수 필드(requirementNo)가 누락되었습니다.")
            
        val version = rootNode.path("version").asText("")
        if (!version.matches(Regex("^\\d+\\.\\d+\\.\\d+$"))) {
            throw IllegalArgumentException("ERR-VAL-002: 비정상적인 버전 포맷입니다. (M.m.p 형식 필요)")
        }

        if (requirementRepository.existsByRequirementNo(requirementNo)) {
            throw IllegalStateException("ERR-DUP-001: 이미 존재하는 요구사항 번호입니다.")
        }
        
        // TODO: file parsing or LOB storage (TC-008, 009, 010, 011, 012)
        
        val response = RequirementResponse(
            id = 1,
            requirementNo = requirementNo,
            status = "Draft",
            createdAt = OffsetDateTime.now()
        )
        
        return requirementRepository.save(response)
    }

    fun updateStatus(id: Int, request: org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest): org.openapitools.model.ApiV1RequirementsIdStatusPatch200Response {
        val requirement = requirementRepository.findById(id)
            ?: throw IllegalArgumentException("ERR-NF-001: 요건을 찾을 수 없습니다.")
        
        val currentStatus = requirement.status ?: "Draft"
        val nextStatusStr = request.status.value
        
        if (currentStatus == "Done") {
            throw IllegalStateException("ERR-STS-002: 완료(Done)된 요건은 상태를 변경할 수 없습니다.")
        }
        
        val validTransitions = mapOf(
            "Draft" to listOf("Clarifying"),
            "Clarifying" to listOf("Clarified"),
            "Clarified" to listOf("In Progress"),
            "In Progress" to listOf("Done")
        )
        
        if (validTransitions[currentStatus]?.contains(nextStatusStr) != true) {
            throw IllegalArgumentException("ERR-STS-001: 유효하지 않은 상태 전이입니다. ($currentStatus -> $nextStatusStr)")
        }
        
        val updated = requirement.copy(
            status = nextStatusStr
        )
        requirementRepository.save(updated)
        
        return org.openapitools.model.ApiV1RequirementsIdStatusPatch200Response(
            id = updated.id,
            status = updated.status,
            updatedAt = java.time.OffsetDateTime.now()
        )
    }
}
