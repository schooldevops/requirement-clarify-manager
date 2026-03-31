// File: src/test/kotlin/org/openapitools/service/AnalysisServiceTest.kt
package org.openapitools.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.*
import org.openapitools.entity.*
import org.openapitools.repository.*
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class AnalysisServiceTest : BehaviorSpec({
    val requirementRepository = mockk<RequirementRepository>()
    val dictionaryRepository = mockk<DataDictionaryRepository>()
    val resultRepository = mockk<AnalysisResultRepository>(relaxed = true)
    val objectMapper = ObjectMapper()
    val service = spyk(AnalysisService(requirementRepository, dictionaryRepository, resultRepository, objectMapper))

    // AnalysisService 의 @Async 메서드가 별도 스레드에서 실행되므로, 테스트 중 호출을 차단하거나 Mock 함
    // 여기서는 triggerAnalysis 에 초점을 맞춤
    
    Given("TC-029: 분석 시작 선행조건 검증 (미정의 용어 잔존)") {
        val requirementId = 1
        val req = RequirementEntity(id = requirementId, projectName = "ProjectA", requirementNo = "REQ-1", version = "1.0.0")
        
        every { requirementRepository.findById(requirementId) } returns Optional.of(req)
        
        When("아직 PENDING 상태인 용어가 데이터 사전에 남아있다면") {
            every { dictionaryRepository.countByProjectNameAndStatus("ProjectA", TermStatus.PENDING) } returns 2
            
            Then("IllegalStateException 예외가 발생한다") {
                val exception = shouldThrow<IllegalStateException> {
                    service.triggerAnalysis(requirementId)
                }
                exception.message shouldBe "ERR-FLOW-001: Cannot start analysis. 2 pending terms remain in the data dictionary."
            }
        }
    }

    Given("TC-028: 분석 파이프라인 Trigger") {
        val requirementId = 1
        val req = RequirementEntity(id = requirementId, projectName = "ProjectA", requirementNo = "REQ-1", version = "1.0.0")
        
        every { requirementRepository.findById(requirementId) } returns Optional.of(req)
        every { dictionaryRepository.countByProjectNameAndStatus("ProjectA", TermStatus.PENDING) } returns 0
        // runAsyncAnalysis 호출 시 오류 방지를 위해 내부 호출 Mocking
        every { service.runAsyncAnalysis(any()) } just runs

        When("모든 용어가 정의된 상태에서 분석을 요청하면") {
            val response = service.triggerAnalysis(requirementId)
            
            Then("정상적으로 분석 작업이 시작되었음을 반환한다") {
                response shouldBe "Analysis job started for requirement: REQ-1"
                verify { service.runAsyncAnalysis(requirementId) }
            }
        }
    }
})
