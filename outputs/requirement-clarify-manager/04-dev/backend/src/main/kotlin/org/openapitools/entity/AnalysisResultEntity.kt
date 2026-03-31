// File: src/main/kotlin/org/openapitools/entity/AnalysisResultEntity.kt
package org.openapitools.entity

import java.time.OffsetDateTime

/**
 * 분석 단계 상태
 */
enum class AnalysisStatus {
    PROCESSING, COMPLETED, FAILED
}

/**
 * Event Storming 및 통합 분석 결과 엔티티 (POJO).
 */
data class AnalysisResultEntity(
    val id: Int = 0,
    val requirementId: Int,
    val actors: String = "[]",         // JSON String
    val commands: String = "[]",       // JSON String
    val domainEvents: String = "[]",    // JSON String
    val aggregates: String = "[]",      // JSON String
    val externalSystems: String = "[]", // JSON String
    val diagramSource: String = "",    // Mermaid source
    val status: AnalysisStatus = AnalysisStatus.PROCESSING,
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
