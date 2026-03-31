// File: src/main/kotlin/org/openapitools/repository/RequirementHistoryRepository.kt
package org.openapitools.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.openapitools.entity.RequirementHistory
import org.openapitools.entity.RequirementStatus
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class RequirementHistoryRepository(private val dsl: DSLContext) {

    private val table = table("requirement_history")
    private val requirementIdField = field("requirement_id", Int::class.java)
    private val fromStatusField = field("from_status", String::class.java)
    private val toStatusField = field("to_status", String::class.java)
    private val changedAtField = field("changed_at", OffsetDateTime::class.java)

    fun save(history: RequirementHistory) {
        dsl.insertInto(table)
            .set(requirementIdField, history.requirementId)
            .set(fromStatusField, history.fromStatus.name)
            .set(toStatusField, history.toStatus.name)
            .set(changedAtField, history.changedAt)
            .execute()
    }
}
