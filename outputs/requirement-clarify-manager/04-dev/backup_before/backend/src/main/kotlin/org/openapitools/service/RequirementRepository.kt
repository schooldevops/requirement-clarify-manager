package org.openapitools.service

import org.openapitools.model.RequirementResponse
import org.springframework.stereotype.Repository

import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryRequirementRepository : RequirementRepository {
    private val db = ConcurrentHashMap<String, RequirementResponse>()
    private var seq = 1

    override fun existsByRequirementNo(requirementNo: String): Boolean {
        return db.values.any { it.requirementNo == requirementNo }
    }

    override fun findById(id: Int): RequirementResponse? {
        return db.values.find { it.id == id }
    }

    override fun save(requirement: RequirementResponse): RequirementResponse {
        val toSave = if (requirement.id == null) {
            requirement.copy(id = seq++)
        } else {
            requirement
        }
        db[toSave.requirementNo!!] = toSave
        return toSave
    }
}

interface RequirementRepository {
    fun existsByRequirementNo(requirementNo: String): Boolean
    fun findById(id: Int): RequirementResponse?
    fun save(requirement: RequirementResponse): RequirementResponse
}
