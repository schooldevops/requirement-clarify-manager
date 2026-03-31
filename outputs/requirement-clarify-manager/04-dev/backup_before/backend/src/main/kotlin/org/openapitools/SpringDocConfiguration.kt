package org.openapitools

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.security.SecurityScheme

@Configuration
class SpringDocConfiguration {

    @Bean
    fun apiInfo(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Requirement Clarify Manager API")
                    .description("요구사항 명세 관리 시스템 (Requirement Clarify Manager) MVP API 사양서. 기획자가 마크다운 파일이나 텍스트로 요구사항을 등록하면, 시스템이 데이터 사전을 추출 및 질의 과정을 거쳐 최종적으로 Event Storming 분석 결과를 시각화하여 제공합니다. ")
                    .version("1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes("BearerAuth", SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                    )
            )
    }
}
