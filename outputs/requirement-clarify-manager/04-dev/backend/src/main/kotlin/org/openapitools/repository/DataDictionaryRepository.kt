// File: src/main/kotlin/org/openapitools/repository/DataDictionaryRepository.kt
package org.openapitools.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.openapitools.entity.DataDictionaryEntity
import org.openapitools.entity.TermStatus
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class DataDictionaryRepository(private val dsl: DSLContext) {

    private val table = table("data_dictionaries")
    private val idField = field("id", Int::class.java)
    private val requirementIdField = field("requirement_id", Int::class.java)
    private val projectNameField = field("project_name", String::class.java)
    private val koreanNameField = field("korean_name", String::class.java)
    private val englishNameField = field("english_name", String::class.java)
    private val dataTypeField = field("data_type", String::class.java)
    private val dataLengthField = field("data_length", Int::class.java)
    private val descriptionField = field("description", String::class.java)
    private val statusField = field("status", String::class.java)
    private val versionField = field("version", Long::class.java)
    private val createdAtField = field("created_at", OffsetDateTime::class.java)
    private val updatedAtField = field("updated_at", OffsetDateTime::class.java)

    fun findById(id: Int): Optional<DataDictionaryEntity> {
        val record = dsl.selectFrom(table).where(idField.eq(id)).fetchOne() ?: return Optional.empty()
        return Optional.of(mapRecordToEntity(record))
    }

    fun findFirstByProjectNameAndStatusOrderByCreatedAtAsc(
        projectName: String,
        status: TermStatus
    ): Optional<DataDictionaryEntity> {
        val record = dsl.selectFrom(table)
            .where(projectNameField.eq(projectName))
            .and(statusField.eq(status.name))
            .orderBy(createdAtField.asc())
            .limit(1)
            .fetchOne() ?: return Optional.empty()

        return Optional.of(mapRecordToEntity(record))
    }

    fun countByProjectNameAndStatus(projectName: String, status: TermStatus): Long {
        return dsl.selectCount()
            .from(table)
            .where(projectNameField.eq(projectName))
            .and(statusField.eq(status.name))
            .fetchOne(0, Long::class.java) ?: 0L
    }

    fun existsByRequirementIdAndKoreanName(requirementId: Int, koreanName: String): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(table)
                .where(requirementIdField.eq(requirementId))
                .and(koreanNameField.eq(koreanName))
        )
    }

    fun findAllByRequirementId(requirementId: Int): List<DataDictionaryEntity> {
        return dsl.selectFrom(table)
            .where(requirementIdField.eq(requirementId))
            .fetch { mapRecordToEntity(it) }
    }

    fun save(entity: DataDictionaryEntity): DataDictionaryEntity {
        return if (entity.id == 0) {
            dsl.insertInto(table)
                .set(requirementIdField, entity.requirementId)
                .set(projectNameField, entity.projectName)
                .set(koreanNameField, entity.koreanName)
                .set(statusField, entity.status.name)
                .set(versionField, entity.version)
                .set(createdAtField, entity.createdAt)
                .set(updatedAtField, entity.updatedAt)
                .returning(idField)
                .fetchOne()?.let { entity.copy(id = it.get(idField)) } ?: entity
        } else {
            // Optimistic Lock Check (TC-026)
            val result = dsl.update(table)
                .set(englishNameField, entity.englishName)
                .set(dataTypeField, entity.dataType)
                .set(dataLengthField, entity.dataLength)
                .set(descriptionField, entity.description)
                .set(statusField, entity.status.name)
                .set(versionField, entity.version + 1)
                .set(updatedAtField, OffsetDateTime.now())
                .where(idField.eq(entity.id))
                .and(versionField.eq(entity.version))
                .execute()

            if (result == 0) {
                throw org.springframework.dao.OptimisticLockingFailureException("Optimistic lock failure for term ${entity.id}")
            }
            entity.copy(version = entity.version + 1, updatedAt = OffsetDateTime.now())
        }
    }

    private fun mapRecordToEntity(record: org.jooq.Record): DataDictionaryEntity {
        return DataDictionaryEntity(
            id = record.get(idField),
            requirementId = record.get(requirementIdField),
            projectName = record.get(projectNameField),
            koreanName = record.get(koreanNameField),
            englishName = record.get(englishNameField),
            dataType = record.get(dataTypeField),
            dataLength = record.get(dataLengthField),
            description = record.get(descriptionField),
            status = TermStatus.valueOf(record.get(statusField)),
            version = record.get(versionField),
            createdAt = record.get(createdAtField),
            updatedAt = record.get(updatedAtField)
        )
    }
}
