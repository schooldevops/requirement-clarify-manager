# MSA(Microservices Architecture) 설계

## 1. 서비스 분리 전략 (Bounded Context)
시스템을 도메인 주도 설계(DDD) 관점으로 2개의 메인 Bounded Context 로 분리합니다.

| 서비스 명 | 역할 / 책임 | Bounded Context | 담당 데이터 모델 |
|---------|---------|---|---|
| **Requirement Service** | 요구사항 생성, 형상 관리(버전 및 이력), 파일 물리 저장 연동 제어, 요구사항 상태 워크플로우 통제 | 요구사항 관리 영역 | REQUIREMENT, REQUIREMENT_HISTORY |
| **Analysis Service** | NLP 추출 지시, 데이터 사전 유지보수(필드 인터랙션), 이벤트 스토밍 요소 등 분석 결과 관리 | 데이터 명확화 및 분석 영역 | DATA_DICTIONARY, EVENT_STORMING_NODE |

> **RATIONALE(설계 결정 근거): ADR-001**
> 파일 업로드 트래픽 및 상태 관리가 주된 '요구사항' 영역과 외부 AI 파이프라인 연계를 통한 장시간의 트랜잭션과 기획자간의 락(동시성) 충돌 등이 잦은 '분석' 영역을 분리함으로써 서로의 장애(예: 분석 AI 엔진 지연)가 요구사항 등록 프로세스에 영향을 미치지 못하도록 격리(Fault Isolation) 설계.

## 2. 서비스 간 통신 방식 (Synchronous vs Asynchronous)

### 2.1 Synchronous (동기 통신)
- **웹 클라이언트 ↔ API Gateway**: React 기반 Front-End가 API 호출을 위해 REST (JSON) 적용.
- **API Gateway ↔ Microservices**: Spring Cloud Gateway 통과 후 각 서비스 WebMVC 엔드포인트 도달 시 HTTP/1.1 REST 통신.
- **Analysis ↔ NLP AI Engine**: 실시간 성격이 강한 NLP 전처리나 빠른 질의는 gRPC를 사용하여 직렬화 비용 감소.

### 2.2 Asynchronous (비동기 통신 - Message Queue)
- **명칭**: 분석 파이프라인 트리거 (Event Bus: Kafka)
- **상황**: 요구사항 등록(API-REQ-001)이 성공하면, Requirement Service는 `RequirementCreatedEvent(req_id, detail)` 이벤트를 Kafka 토픽에 발행합니다.
- **결과**: Analysis Service가 워커 쓰레드를 통해 컨슘하고 백그라운드에서 지연 시간 없이 외부 확장 엔진으로 단어 추출 파이프라인 로직을 이관합니다. 완료된 후 Analysis Service가 Kafka나 Pub/Sub을 통해 SSE(Websocket) 알림용으로 이벤트를 추가 발행하여 프론트엔드로 전달합니다.

## 3. API Gateway 설계
- **프레임워크**: Spring Cloud Gateway
- **라우팅 규칙**:
  - `/api/v1/requirements/**` -> `req-service` 라우팅
  - `/api/v1/projects/**` -> `analysis-service` 라우팅 (데이터 딕셔너리 명확화 등)
  - `/api/v1/requirements/{id}/analyze` -> `analysis-service` 로 라우팅 (경로 패턴 일치 우선순위 부여)
- **인증/인가 (Security)**: API 게이트웨이 레벨에서 Global Filter를 통해 JWT 토큰 Signature 검증 및 Expire 체크. (서비스 레벨에서는 파싱된 Header의 User ID만 신뢰하여 사용)
- **Rate Limiting**: Redis를 연동하여 IP + Token 단위로 분당 100회 요청 제한(분석 API는 분당 10회 등 차등 제한) 설정.

## 4. Service Discovery & Circuit Breaker
- **Service Discovery**: Kubernetes 내부 DNS 서버 (CoreDNS)의 Service Name Resolution 기능 사용. (유레카 배제)
- **Circuit Breaker (Resilience4j)**:
  - **위치**: Analysis Service 내부의 외부 AI/NLP Engine 호출 구간.
  - **정책**: 실패율 50% 이상 or Timeout 발생률 임계치 도달시 Circuit Open.
  - **Fallback**: Open 시 `ERR-EXT-001` 응답 반환 및 "잠시 후 다시 시도해주세요" 더미/캐시 데이터 제공을 통해 스레드 풀 고갈 방지.
