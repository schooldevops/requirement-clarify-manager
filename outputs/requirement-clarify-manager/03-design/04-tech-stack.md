# 기술 스택 명세

## 1. Backend (Microservices)
- **Framework**: Spring Boot 3.5.x / Spring WebMVC
- **Language**: Kotlin 2.0.x (코루틴 및 null-safety 이점을 활용)
- **ORM / DB Access**: Spring Data JPA / Querydsl
- **API Documentation**: SpringDoc OpenAPI UI
- **선정 이유**: 엔터프라이즈 환경에서의 높은 안정성 유지. Kotlin을 활용하여 Null 안정성과 간결한 코드를 통해 도메인 로직 복잡성을 완화함.

## 2. Frontend
- **Framework**: Next.js 14.x (App Router 적용) / React 18.x
- **Language**: TypeScript
- **State Management**: Zustand (전역 상태), React Query (서버 상태 및 비동기 캐싱 관리)
- **Styling**: TailwindCSS
- **선정 이유**: SSR을 통한 첫 뷰 로딩 속도 최적화. React Query를 통해 잦은 폴링 구조나 연속된 GET/POST 인터랙션(데이터 딕셔너리 명확화 로직)을 손쉽게 캐싱 및 동기화 처리.

## 3. Database
- **RDBMS**: PostgreSQL 16.x
- **NoSQL / Cache**: Redis 7.x
- **선정 이유**: 
  - PostgreSQL은 JSONB 지원과 함께 LOB/Text 처리에 강력하며 동시성 처리가 우수함. 데이터 사전 및 요구사항 메인 마스터로 적합.
  - Redis는 API Gateway의 Rate Limiter 및 MSA 분산 환경의 분산 락, 토큰 캐싱 용도로 사용.

## 4. Message Broker (Event Bus)
- **Broker**: Apache Kafka 3.6.x
- **선정 이유**: 비동기 파이프라인 분리 원칙(ADR-001)에 따라 요구사항 등록 시 후속 단어 추출/분석 등의 과정 결합도를 낮추기 위함. RabbitMQ 대비 Event 재처리(Replay) 특성이 외부 AI 시스템 장애 전파 차단 관점에서 유리함.

## 5. Infrastructure & Orchestration
- **Containerization**: Docker
- **Orchestration**: Kubernetes 1.29 (AWS EKS 기반)
- **Service Mesh**: Istio 1.21.x (MSA 내 트래픽 라우팅, mTLS, Observability 등 통합 제어)
- **CI/CD**: GitHub Actions / ArgoCD (GitOps 인프라 배포)
- **선정 이유**: 선언적인 아키텍처 배포 관리(GitOps)를 통한 프로덕션 배포 파이프라인의 안전성 강화 및 Service Mesh 로 MSA 간 통신 모니터링 체계 완성을 목적.

## 6. 기타 도구 (Observability & NLP)
- **Logging / Tracing**: ELK Stack (Logstash, Elasticsearch), Jaeger (Distributed Tracing / OpenTelemetry)
- **외부 NLP 연계**: Python (FastAPI/gRPC Server 기반의 KoNLPy 엔진 래퍼 혹은 내부 LLM 연동망)
- **선정 이유**: 복잡한 MSA 통신 구조의 병목 현상과 트랜잭션 추적을 위해 OpenTelemetry 표준과 Jaeger 채택.
