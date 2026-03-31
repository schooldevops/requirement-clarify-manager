// File: src/main/kotlin/org/openapitools/repository/AnalysisResultRepository.kt
package org.openapitools.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.openapitools.entity.AnalysisResultEntity
import org.openapitools.entity.AnalysisStatus
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class AnalysisResultRepository(private val dsl: DSLContext) {

    private val table = table("analysis_results")
    private val idField = field("id", Int::class.java)
    private val requirementIdField = field("requirement_id", Int::class.java)
    private val actorsField = field("actors", String::class.java)
    private val commandsField = field("commands", String::class.java)
    private val domainEventsField = field("domain_events", String::class.java)
    private val aggregatesField = field("aggregates", String::class.java)
    private val externalSystemsField = field("external_systems", String::class.java)
    private val diagramSourceField = field("diagram_source", String::class.java)
    private val statusField = field("status", String::class.java)
    private val createdAtField = field("created_at", OffsetDateTime::class.java)

    fun findByRequirementId(requirementId: Int): Optional<AnalysisResultEntity> {
        val record = dsl.selectFrom(table)
            .where(requirementIdField.eq(requirementId))
            .fetchOne() ?: return Optional.empty()

        return Optional.of(AnalysisResultEntity(
            id = record.get(idField),
            requirementId = record.get(requirementIdField),
            actors = record.get(actorsField) ?: "[]",
            commands = record.get(commandsField) ?: "[]",
            domainEvents = record.get(domainEventsField) ?: "[]",
            aggregates = record.get(aggregatesField) ?: "[]",
            externalSystems = record.get(externalSystemsField) ?: "[]",
            diagramSource = record.get(diagramSourceField) ?: "",
            status = AnalysisStatus.valueOf(record.get(statusField)),
            createdAt = record.get(createdAtField)
        ))
    }

    fun save(entity: AnalysisResultEntity) {
        // Upsert logic (MVP: Delete and Insert for simple unique requirement_id)
        dsl.deleteFrom(table).where(requirementIdField.eq(entity.requirementId)).execute()

        dsl.insertInto(table)
            .set(requirementIdField, entity.requirementId)
            .set(actorsField, entity.actors)
            .set(commandsField, entity.commands)
            .set(domainEventsField, entity.domainEvents)
            .set(aggregatesField, entity.aggregates)
            .set(externalSystemsField, entity.externalSystems)
            .set(diagramSourceField, entity.diagramSource)
            .set(statusField, entity.status.name)
            .set(createdAtField, entity.createdAt)
            .execute()
    }
}
