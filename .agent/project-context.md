# Project Context: Requirement Clarify Manager (요구사항 명세 관리 시스템)

## Context Management
컨텍스트 관리는 project-context.md 파일에 요건 내용을 차근차근 기록하고, 결과물은 outputs 폴더 하위에 요청이름/하위디렉토리에 순서대로 확인할 수 있도록 번호를 매겨서 생성하여 컨텍스트를 관리해야한다.
project-context-[projecdt-name].md 파일 을 만들어 관리해주고, outputs/[projecdt-name] 폴더에 결과물을 생성해야한다.

## 프로젝트 정보 형식
- **프로젝트명**: requirement-clarify-manager
- **요청 부서**: 기획/사업팀
- **시작일**: 2026-03-26
- **현재 단계**: 03. 설계 단계 (Interface Agent 작업 완료, HITL 대기중)

## 완료된 산출물 형식
- [x] 요건정의서 (v1.0) - `outputs/requirement-clarify-manager/01-requirements/01-requirements-spec.md`
  - 6개 주요 기능 요구사항 정의
  - 2개 비기능 요구사항 정의
  - 10개 핵심 예외 케이스 식별
- [x] 테스트 케이스 (v1.0) - `outputs/requirement-clarify-manager/01-requirements/02-test-cases.md`
  - 도출 가능한 36개 포괄적 TC(정상/예외/경계/보안) 목록 작성 완료
- [x] 용어 정의서 (v1.0) - `outputs/requirement-clarify-manager/01-requirements/03-data-dictionary.md`
  - 10개 핵심 도메인 용어 정의
- [x] 인터페이스 정의서 (v1.0) - `outputs/requirement-clarify-manager/02-analysis/01-interface-spec.md`
  - 6개 주요 API 엔드포인트 정의
  - 요청/응답 페이로드 및 에러 코드 명세
- [x] 비즈니스 로직 상세 (v1.0) - `outputs/requirement-clarify-manager/02-analysis/02-business-logic-detail.md`
  - 요구사항 등록, 상태 변경, 데이터 사전 추출/분석 등의 처리 흐름 및 규칙 작성
- [x] 데이터 모델 (v1.0) - `outputs/requirement-clarify-manager/02-analysis/03-data-model.md`
  - 5개 핵심 엔티티(프로젝트, 요구사항, 이력, 사전 등) ERD 작성 및 인덱스/파티셔닝 전략 명세
- [x] 시퀀스 다이어그램 (v1.0) - `outputs/requirement-clarify-manager/02-analysis/04-sequence-diagrams.md`
  - 요구사항 등록/분석 비동기 파이프라인 및 용어 명확화 흐름(낙관적 락 처리 포함) 작성
- [x] OpenAPI 스펙 (v1.0) - `outputs/requirement-clarify-manager/03-design/01-openapi.yaml`
  - 6개 API 엔드포인트 정의 (마크다운 파일 등록, 추출 트리거 등)
  - OpenAPI 3.0 스펙 기반 데이터 모델 스키마 정의
- [x] 시스템 아키텍처 (v1.0) - `outputs/requirement-clarify-manager/03-design/02-architecture.md`
  - C4 모델 (Context, Container) 기반 다이어그램 작성
  - Hexagonal 레이어드 아키텍처 구조 명세
  - EKS 컨테이너 기반 배포 아키텍처 설계
- [x] MSA 설계 (v1.0) - `outputs/requirement-clarify-manager/03-design/03-msa-design.md`
  - 요구사항 서비스와 분석 서비스 분리 (Bounded Context)
  - Kafka를 활용한 비동기 분석 파이프라인(Event Bus) 분리 통신 명세
  - Spring Cloud Gateway 및 Resilience4j 서킷브레이커 적용
- [x] 기술 스택 (v1.0) - `outputs/requirement-clarify-manager/03-design/04-tech-stack.md`
  - Backend: Spring Boot 3.3.x, Kotlin 2.0.x
  - Frontend: Next.js 14.x, React Query, Zustand
  - DB/Cache: PostgreSQL 16.x, Redis 7.x
  - Infra: K8s, Istio, Kafka, ELK, Jaeger 등

## 주요 요구사항 요약
요구사항 관리를 위한 시스템으로, 기획자가 작성한 요구사항 파일(.md)이나 직접 입력된 요구사항을 분석하여 
명사/동사를 추출해 데이터 사전을 자동 생성합니다. 이후 미정의 용어에 대해 피드백 루프를 돌고,
데이터 사전이 완료되면 Event Storming 분석을 수행하여 결과를 다양한 뷰(도메인 객체, 시각화 보드 등)로 제공합니다.

### 핵심 예외 케이스
대용량 텍스트 처리 중 타임아웃, 동시 수정 충돌, 요구사항 번호 중복 등.

## 산출물 디렉토리 구조
`outputs/requirement-clarify-manager/` 하위에서 관리.