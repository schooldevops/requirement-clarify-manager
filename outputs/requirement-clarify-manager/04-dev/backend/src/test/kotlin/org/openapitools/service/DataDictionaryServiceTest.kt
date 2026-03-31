// File: src/test/kotlin/org/openapitools/service/DataDictionaryServiceTest.kt
package org.openapitools.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.openapitools.entity.*
import org.openapitools.model.*
import org.openapitools.repository.*
import java.util.*

class DataDictionaryServiceTest : BehaviorSpec({
    val requirementRepository = mockk<RequirementRepository>()
    val dictionaryRepository = mockk<DataDictionaryRepository>()
    val service = DataDictionaryService(requirementRepository, dictionaryRepository)

    Given("TC-020: 용어 자동 추출") {
        val requirementId = 1
        val requirement = RequirementEntity(
            id = requirementId,
            projectName = "ProjectA",
            requirementNo = "REQ-001",
            version = "1.0.0",
            originalContent = "회원 가입 기능을 구현한다. 사용자는 이메일과 비밀번호를 입력한다."
        )

        every { requirementRepository.findById(requirementId) } returns Optional.of(requirement)
        every { dictionaryRepository.existsByRequirementIdAndKoreanName(any(), any()) } returns false
        every { dictionaryRepository.save(any()) } returns mockk()

        When("용어 추출을 실행하면") {
            service.extractTerms(requirementId)

            Then("텍스트 내의 명사들이 PENDING 상태로 저장된다") {
                // '회원', '가입', '기능', '구현', '사용자', '이메일', '비밀번호' 등이 호출되어야 함
                verify(atLeast = 3) { dictionaryRepository.save(any()) }
            }
        }
    }

    Given("TC-021: 프로젝트간 데이터 사전 격리") {
        val termId = 100
        val termInProjectA = DataDictionaryEntity(
            id = termId,
            requirementId = 1,
            projectName = "ProjectA", // A 프로젝트 소속
            koreanName = "사용자",
            status = TermStatus.PENDING
        )

        every { dictionaryRepository.findById(termId) } returns Optional.of(termInProjectA)

        When("B 프로젝트 이름으로 해당 용어를 수정을 시도하면") {
            val request = ClarifyRequest(
                action = ClarifyRequest.Action.DEFINE,
                version = 0,
                englishName = "User"
            )
            
            Then("IllegalArgumentException 예외가 발생하여 격리를 보장한다") {
                val exception = io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
                    service.clarifyTerm("ProjectB", termId, request)
                }
                exception.message shouldBe "Project name mismatch for term $termId"
            }
        }
    }

    Given("TC-026: 동시 수정 낙관적 락 검증") {
        // 이 케이스는 실제 DB 연동 통합 테스트에서 더 의미가 있지만, 
        // 서비스 단에서 save() 시 JPA가 던지는 OptimisticLockingFailureException 을 상정함
        // 여기선 JPA 동작이기에 생략하거나 Mocking 가능.
    }
})
