// File: src/main/kotlin/org/openapitools/repository/RequirementRepository.kt
package org.openapitools.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.openapitools.entity.RequirementEntity
import org.openapitools.entity.RequirementStatus
import org.springframework.stereotype.Repository
import java.util.*
import java.time.OffsetDateTime

@Repository
class RequirementRepository(private val dsl: DSLContext) {

    private val table = table("requirements")
    private val idField = field("id", Int::class.java)
    private val projectNameField = field("project_name", String::class.java)
    private val requirementNoField = field("requirement_no", String::class.java)
    private val versionField = field("version", String::class.java)
    private val statusField = field("status", String::class.java)
    private val originalContentField = field("original_content", String::class.java)
    private val updatedAtField = field("updated_at", OffsetDateTime::class.java)

    fun existsByRequirementNo(requirementNo: String): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(table).where(requirementNoField.eq(requirementNo))
        )
    }

    fun findById(id: Int): Optional<RequirementEntity> {
        val record = dsl.selectFrom(table).where(idField.eq(id)).fetchOne() ?: return Optional.empty()
        
        return Optional.of(RequirementEntity(
            id = record.get(idField),
            projectName = record.get(projectNameField),
            requirementNo = record.get(requirementNoField),
            version = record.get(versionField),
            status = RequirementStatus.valueOf(record.get(statusField)),
            originalContent = record.get(originalContentField) ?: "",
            updatedAt = record.get(updatedAtField)
        ))
    }

    fun save(entity: RequirementEntity): RequirementEntity {
        return if (entity.id == 0) {
            val record = dsl.insertInto(table)
                .set(projectNameField, entity.projectName)
                .set(requirementNoField, entity.requirementNo)
                .set(versionField, entity.version)
                .set(statusField, entity.status.name)
                .set(originalContentField, entity.originalContent)
                .set(updatedAtField, OffsetDateTime.now())
                .returning(idField, projectNameField, requirementNoField, versionField, statusField, originalContentField, updatedAtField)
                .fetchOne()!!

            RequirementEntity(
                id = record.get(idField),
                projectName = record.get(projectNameField),
                requirementNo = record.get(requirementNoField),
                version = record.get(versionField),
                status = RequirementStatus.valueOf(record.get(statusField)),
                originalContent = record.get(originalContentField) ?: "",
                updatedAt = record.get(updatedAtField)
            )
        } else {
            dsl.update(table)
                .set(projectNameField, entity.projectName)
                .set(requirementNoField, entity.requirementNo)
                .set(versionField, entity.version)
                .set(statusField, entity.status.name)
                .set(originalContentField, entity.originalContent)
                .set(updatedAtField, OffsetDateTime.now())
                .where(idField.eq(entity.id))
                .execute()
            entity
        }
    }
}
