// File: src/test/kotlin/org/openapitools/service/RequirementServiceTest.kt
package org.openapitools.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.openapitools.entity.RequirementEntity
import org.openapitools.entity.RequirementStatus
import org.openapitools.exception.DuplicateRequirementException
import org.openapitools.repository.RequirementHistoryRepository
import org.openapitools.repository.RequirementRepository
import java.util.*

class RequirementServiceTest : BehaviorSpec({
    val requirementRepository = mockk<RequirementRepository>()
    val historyRepository = mockk<RequirementHistoryRepository>()
    val objectMapper = jacksonObjectMapper()
    val requirementService = RequirementService(requirementRepository, historyRepository, objectMapper)

    Given("TC-001: 요구사항 직접 입력 정상 저장") {
        val requestData = """{"projectName":"Project A","requirementNo":"REQ-100","version":"1.0.0"}"""
        every { requirementRepository.existsByRequirementNo(any()) } returns false
        every { requirementRepository.save(any()) } returns RequirementEntity(
            id = 1,
            projectName = "Project A",
            requirementNo = "REQ-100",
            version = "1.0.0",
            status = RequirementStatus.Draft,
            originalContent = ""
        )

        When("정상적인 요구사항 데이터를 저장하면") {
            val result = requirementService.createRequirement(requestData, null)

            Then("상태가 Draft 인 상태로 성공적으로 저장된다") {
                result.id shouldBe 1
                result.requirementNo shouldBe "REQ-100"
                result.status shouldBe "Draft"
                verify(exactly = 1) { requirementRepository.save(any()) }
            }
        }
    }

    // ── Phase 2: 상태 전이 (TC-013 ~ TC-019) ───────────────────────────

    Given("상태 전이 시나리오") {
        val existingReq = RequirementEntity(
            id = 1,
            projectName = "Project A",
            requirementNo = "REQ-100",
            version = "1.0.0",
            status = RequirementStatus.Draft
        )

        every { requirementRepository.findById(1) } returns Optional.of(existingReq)
        every { requirementRepository.save(any()) } answers { it.invocation.args[0] as RequirementEntity }
        every { historyRepository.save(any()) } returns mockk()

        When("TC-013: Draft -> Clarifying 으로 상태를 변경하면") {
            val result = requirementService.updateStatus(1, "Clarifying")
            Then("정상적으로 변경되고 이력이 저장된다") {
                result.status shouldBe RequirementStatus.Clarifying
                verify(exactly = 1) { historyRepository.save(any()) }
            }
        }

        When("TC-017: Clarifying 에서 바로 Done 으로 변경을 시도하면 (Skip)") {
            Then("InvalidStatusTransitionException 예외가 발생한다") {
                shouldThrow<InvalidStatusTransitionException> {
                    requirementService.updateStatus(1, "Done")
                }
            }
        }

        When("TC-019: 상태가 Done 인 요구사항을 수정하려 하면") {
            existingReq.status = RequirementStatus.Done
            Then("IllegalStateException 예외가 발생한다") {
                shouldThrow<IllegalStateException> {
                    requirementService.updateStatus(1, "Draft")
                }
            }
        }
    }
})
