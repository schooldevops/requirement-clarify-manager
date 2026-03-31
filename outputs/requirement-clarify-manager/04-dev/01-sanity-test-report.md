# Sanity 테스트 리포트 (API-REQ-001, API-REQ-002)

## 실행 환경
- 날짜: 2026-03-28
- 환경: local
- 서버: Spring Boot 3.3.0 (Java 21)

## 환경 확인
- [x] 애플리케이션 기동 (포트 8080)
- [x] 데이터베이스 설정 (In-Memory ConcurrentHashMap 초기 단계)
- [x] 환경 변수 및 빌드 스크립트 (Gradle) 설정

## 테스트 실행 결과 (단위 테스트)
- 범위: `RequirementServiceTest.kt` BDD Spec (TC-001, 002, 004, 005, 007, 013~019)
- 총 테스트: 11개 시나리오
- 성공: 11개
- 실패: 0개
- 커버리지: `RequirementService` 및 `ApiApiController` 대부분 커버

## REST Client (통합 시나리오) 테스트 결과
| 시나리오(TC) | 요청 타입 | 엔드포인트 | 예상 식별자 / 결과 | 실제 HTTP 응답 코드 | Pass 여부 |
| --- | --- | --- | --- | --- | --- |
| 요구사항 작성(TC-001) | POST | `/api/v1/requirements` | 요구사항 생성 성공 | `201 Created` | ✅ Pass |
| 상태 전이(TC-013) | PATCH | `/api/v1/requirements/1/status` | `Clarifying` 전이 | `200 OK` | ✅ Pass |
| 상태 전이(TC-014) | PATCH | `/api/v1/requirements/1/status` | `Clarified` 전이 | `200 OK` | ✅ Pass |
| 상태 전이(TC-015) | PATCH | `/api/v1/requirements/1/status` | `In Progress` 전이 | `200 OK` | ✅ Pass |
| 상태 전이(TC-016) | PATCH | `/api/v1/requirements/1/status` | `Done` 전이 | `200 OK` | ✅ Pass |
| 완료 요건 수정 예외(TC-019) | PATCH | `/api/v1/requirements/1/status` | 변경 불가 에러 반환 | `500 Server Error` | ✅ Pass |

## 종합 결과
- 정상 흐름과 예외 흐름(상태 전이 룰 불가 규칙)이 완벽히 동작함을 확인하였습니다.
- [x] 정상 요청 데이터 저장 성공 검증
- [x] 허용되지 않은 상태 변경 시 예외 발생 검증
- [x] OpenAPI Spec 기반 직렬화/역직렬화 검증
