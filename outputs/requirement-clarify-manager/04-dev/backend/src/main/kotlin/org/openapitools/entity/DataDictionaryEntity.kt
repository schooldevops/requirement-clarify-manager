// File: src/main/kotlin/org/openapitools/entity/DataDictionaryEntity.kt
package org.openapitools.entity

import java.time.OffsetDateTime

/**
 * 데이터 사전 용어 상태
 */
enum class TermStatus {
    PENDING, DEFINED, SKIPPED
}

/**
 * 데이터 사전 데이터 클래스.
 */
data class DataDictionaryEntity(
    val id: Int = 0,
    val requirementId: Int,
    val projectName: String,
    val koreanName: String,
    var englishName: String? = null,
    var dataType: String? = null,
    var dataLength: Int? = null,
    var description: String? = null,
    var status: TermStatus = TermStatus.PENDING,
    var version: Long = 0,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
