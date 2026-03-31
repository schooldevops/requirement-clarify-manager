package org.openapitools.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.openapitools.model.RequirementResponse
import org.springframework.web.multipart.MultipartFile

class RequirementServiceTest : BehaviorSpec({
    val requirementRepository = mockk<RequirementRepository>()
    val requirementService = RequirementService(requirementRepository)

    Given("유효한 수기입력 요구사항 데이터가 주어졌을 때 (TC-001, TC-005)") {
        val requestData = """
            {
                "requirementNo": "REQ-123",
                "requirementType": "NEW",
                "projectName": " requirement-clarify-manager",
                "categoryName": "요구사항 관리",
                "requirementName": "요구사항 명세 관리 시스템 구축",
                "authorName": "홍길동",
                "version": "1.0.0",
                "overview": "요구사항에 대한 개략적인 개요...",
                "detail": "수동 입력시 상세 내용"
            }
        """.trimIndent()
        
        every { requirementRepository.existsByRequirementNo("REQ-123") } returns false
        every { requirementRepository.save(any()) } answers { firstArg() }

        When("요구사항을 등록하면") {
            val result = requirementService.createRequirement(requestData, null)

            Then("요구사항이 성공적으로 생성되어야 한다") {
                result.requirementNo shouldBe "REQ-123"
                result.status shouldBe "Draft"
                verify(exactly = 1) { requirementRepository.save(any()) }
            }
        }
    }

    Given("필수 필드(요구사항 번호)가 누락된 데이터가 주어졌을 때 (TC-002)") {
        val invalidRequestData = """
            {
                "requirementType": "NEW",
                "requirementName": "요구사항 명세 관리 시스템 구축",
                "version": "1.0.0",
                "detail": "상세 내용"
            }
        """.trimIndent()

        When("요구사항을 등록하면") {
            Then("유효성 검증 예외(ERR-VAL-001)가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    requirementService.createRequirement(invalidRequestData, null)
                }
                exception.message shouldBe "ERR-VAL-001: 필수 필드(requirementNo)가 누락되었습니다."
            }
        }
    }

    Given("비정상적인 버전 포맷이 주어졌을 때 (TC-004)") {
        val invalidVersionData = """
            {
                "requirementNo": "REQ-124",
                "requirementType": "NEW",
                "requirementName": "요구사항",
                "version": "1.0",
                "detail": "상세 내용"
            }
        """.trimIndent()

        When("요구사항을 등록하면") {
            Then("버전 포맷 예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    requirementService.createRequirement(invalidVersionData, null)
                }
                exception.message shouldBe "ERR-VAL-002: 비정상적인 버전 포맷입니다. (M.m.p 형식 필요)"
            }
        }
    }

    Given("이미 존재하는 요구사항 번호가 주어졌을 때 (TC-007)") {
        val duplicateData = """
            {
                "requirementNo": "REQ-100",
                "requirementType": "NEW",
                "requirementName": "요구사항",
                "version": "1.0.0",
                "detail": "상세 내용"
            }
        """.trimIndent()
        
        every { requirementRepository.existsByRequirementNo("REQ-100") } returns true

        When("요구사항을 등록하면") {
            Then("중복 예외(ERR-DUP-001)가 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    requirementService.createRequirement(duplicateData, null)
                }
                exception.message shouldBe "ERR-DUP-001: 이미 존재하는 요구사항 번호입니다."
            }
        }
    }

    Given("요구사항 상태 변경 시 (TC-013 ~ TC-019)") {
        val existingRequirement = RequirementResponse(
            id = 1,
            requirementNo = "REQ-001",
            status = "Draft",
            createdAt = java.time.OffsetDateTime.now()
        )
        
        every { requirementRepository.findById(1) } returns existingRequirement.copy()
        every { requirementRepository.findById(999) } returns null
        every { requirementRepository.save(any()) } answers { firstArg() }

        When("존재하지 않는 요구사항 ID로 요청하면") {
            val request = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest(
                status = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest.Status.Clarifying
            )
            val exception = shouldThrow<IllegalArgumentException> {
                requirementService.updateStatus(999, request)
            }
            Then("ERR-NF-001 예외가 발생한다") {
                exception.message shouldBe "ERR-NF-001: 요건을 찾을 수 없습니다."
            }
        }

        When("Draft 상태에서 Clarifying로 변경하면 (TC-013)") {
            val request = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest(
                status = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest.Status.Clarifying
            )
            every { requirementRepository.findById(1) } returns existingRequirement.copy(status = "Draft")
            val result = requirementService.updateStatus(1, request)
            
            Then("상태가 성공적으로 변경된다") {
                result.status shouldBe "Clarifying"
            }
        }

        When("Clarifying 상태에서 Done으로 건너뛰어 변경 시도하면 (TC-018)") {
            val request = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest(
                status = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest.Status.Done
            )
            every { requirementRepository.findById(1) } returns existingRequirement.copy(status = "Clarifying")
            
            val exception = shouldThrow<IllegalArgumentException> {
                requirementService.updateStatus(1, request)
            }
            Then("상태 전이 예외(ERR-STS-001)가 발생한다") {
                exception.message shouldBe "ERR-STS-001: 유효하지 않은 상태 전이입니다. (Clarifying -> Done)"
            }
        }

        When("Done 상태에서 Draft로 변경 시도하면 (TC-019)") {
            val request = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest(
                status = org.openapitools.model.ApiV1RequirementsIdStatusPatchRequest.Status.Draft
            )
            every { requirementRepository.findById(1) } returns existingRequirement.copy(status = "Done")
            
            val exception = shouldThrow<IllegalStateException> {
                requirementService.updateStatus(1, request)
            }
            Then("완료 상태 변경 불가 예외(ERR-STS-002)가 발생한다") {
                exception.message shouldBe "ERR-STS-002: 완료(Done)된 요건은 상태를 변경할 수 없습니다."
            }
        }
    }
})
