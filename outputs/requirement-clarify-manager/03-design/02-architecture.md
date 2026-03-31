# 시스템 아키텍처

## 1. C4 Model - Context Diagram
요구사항 명세 관리 시스템(Requirement Clarify Manager)이 외부 액터 및 시스템과 어떻게 상호작용하는지 보여줍니다.

```mermaid
C4Context
    title System Context diagram for Requirement Clarify Manager

    Person(planner, "기획자", "요구사항을 등록하고 데이터 사전 누락 항목을 정의하는 주체")
    
    System(rcm, "Requirement Clarify Manager", "요구사항 접수, 데이터 추출, 이벤트 스토밍 분석 파이프라인 관장 시스템")
    
    System_Ext(jira, "JIRA System", "요구사항 번호(티켓명)의 유효성 위임 및 연동 시스템")
    System_Ext(nlp, "AI/NLP Engine", "자연어 기반 형태소 분석 및 이벤트 스토밍 요소 식별 외부 엔진")
    System_Ext(s3, "Object Storage", "대용량 요구사항 Markdown 파일 등 LOB 정적 데이터 영구 저장소")

    Rel(planner, rcm, "요구사항(.md) 등록, 누락 용어 입력", "HTTPS")
    Rel(rcm, jira, "티켓 정보 확인 (Future Scope)", "REST API")
    Rel(rcm, nlp, "텍스트 분석 의뢰 및 Callback", "gRPC / Async Event")
    Rel(rcm, s3, "첨부파일 및 대용량 요구사항 Text 저장", "S3 API")
```

## 2. C4 Model - Container Diagram
내부 컨테이너 수준의 시스템 구성도입니다. MSA로 분리되어 동작합니다.

```mermaid
C4Container
    title Container diagram for Requirement Clarify Manager

    Person(planner, "기획자", "시스템 사용자")

    Container(webApp, "Web UI", "Next.js", "사용자 조작을 위한 Frontend Web Application")
    Container(apiGateway, "API Gateway", "Spring Cloud Gateway", "라우팅, 인증/인가, Rate Limiting")
    
    Container(reqService, "Requirement Service", "Spring Boot", "요구사항 마스터 데이터 관리 및 형상 관리")
    Container(analysisService, "Analysis Service", "Spring Boot", "용어 명확화, AI 분석 파이프라인 오케스트레이션")

    ContainerDb(reqDb, "Requirement DB", "PostgreSQL", "요구사항, 히스토리, 파일 메타데이터 저장")
    ContainerDb(analysisDb, "Analysis DB", "PostgreSQL", "데이터 사전, Event Storming 분석 결과 저장")
    
    ContainerQueue(kafka, "Event Bus", "Kafka", "비동기 분석 요청, 상태 변경 이벤트 라우팅")
    System_Ext(nlp, "AI/NLP Engine", "자연어 기반 형태소 분석 및 이벤트 스토밍 요소 식별 외부 엔진")

    Rel(planner, webApp, "사용", "HTTPS")
    Rel(webApp, apiGateway, "API 호출", "HTTPS/JSON")
    Rel(apiGateway, reqService, "요구사항 관련 요청", "REST")
    Rel(apiGateway, analysisService, "분석 및 명확화 요청", "REST")
    
    Rel(reqService, reqDb, "읽기/쓰기", "JDBC")
    Rel(reqService, kafka, "분석 파이프라인 트리거(Event)", "Kafka Producer")
    
    Rel(analysisService, analysisDb, "읽기/쓰기", "JDBC")
    Rel(kafka, analysisService, "분석 작업 수신(Event)", "Kafka Consumer")
    Rel(analysisService, nlp, "AI 엔진 분석 위임", "gRPC")
```

## 3. 레이어 아키텍처 (Layered Architecture)
각 개별 마이크로서비스는 **Hexagonal Architecture (Ports and Adapters)** 패턴을 따릅니다.
- **Presentation Layer (Inbound Adapter)**: REST Controller, Event Listener (Kafka Consumer)
- **Application Layer (Use Case)**: 비즈니스 흐름 오케스트레이션 수행. `@Transactional` 관리, 컴포넌트 간 조율.
- **Domain Layer**: 엔티티 생명주기 제어 및 핵심 비즈니스 로직(예: 버전 포맷 검증 규칙, 상태 전이 규칙) 캡슐화. 이 영역은 외부 의존성이 없어야 함.
- **Infrastructure Layer (Outbound Adapter)**: Spring Data JPA DB Query, 외부 AI Engine 연동 gRPC Client, S3 업로더 등 외부 시스템/저장소와의 연동.

## 4. 배포 아키텍처 (Deployment Architecture)
- **Orchestration**: Kubernetes(EKS) 클러스터 상에 컨테이너화 되어 배포.
- **Ingress**: AWS ALB Ingress Controller를 사용해 API Gateway로 트래픽 라우팅.
- **Scalability**: Web UI, API Gateway, Requirement/Analysis Service 모듈들 모두 HPA(Horizontal Pod Autoscaler) 설정으로 CPU/Memory 기반 오토 스케일링 구성.
- **비동기 처리**: AI 분석 작업의 지연을 대비하여 Analysis Service 워커 풀 격리 및 Kafka 파티션을 통한 분산 처리 적용.
