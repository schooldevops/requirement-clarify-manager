// File: src/main/kotlin/org/openapitools/entity/RequirementEntity.kt
package org.openapitools.entity

import java.time.OffsetDateTime

/**
 * 요구사항 상태 정의
 */
enum class RequirementStatus(val value: String) {
    Draft("Draft"),
    Clarifying("Clarifying"),
    Clarified("Clarified"),
    InProgress("In Progress"),
    Done("Done");

    companion object {
        fun fromValue(value: String): RequirementStatus {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown status value: $value")
        }
    }
}

/**
 * 요구사항 데이터 클래스 (jOOQ 매핑용 POJO 역할을 함).
 */
data class RequirementEntity(
    val id: Int = 0,
    val projectName: String,
    val requirementNo: String,
    val version: String,
    var status: RequirementStatus = RequirementStatus.Draft,
    val originalContent: String = "",
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

/**
 * 상태 변경 이력 데이터 클래스.
 */
data class RequirementHistory(
    val id: Long = 0,
    val requirementId: Int,
    val fromStatus: RequirementStatus,
    val toStatus: RequirementStatus,
    val changedAt: OffsetDateTime = OffsetDateTime.now(),
    val reason: String? = null
)
