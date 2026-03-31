# 인터페이스 정의서

## 1. API 목록
| API-ID | 엔드포인트 | 메서드 | 설명 | 인증 필요 |
|--------|-----------|--------|------|---------|
| API-REQ-001 | `/api/v1/requirements` | POST | 요구사항 등록 (파일 업로드 또는 직접 입력) | Y |
| API-REQ-002 | `/api/v1/requirements/{id}/status` | PATCH | 요구사항 상태 변경 | Y |
| API-REQ-003 | `/api/v1/requirements/{id}/data-dictionary/extract` | POST | 데이터 사전 자동 생성 추츨 | Y |
| API-REQ-004 | `/api/v1/projects/{projectName}/data-dictionary/pending` | GET | 누락(미정의) 용어 단건 조회 (다음 명확화 대상) | Y |
| API-REQ-007 | `/api/v1/projects/{projectName}/data-dictionary/{termId}/clarify` | POST | 단일 용어 질의 피드백 (필드 단위 명확화) | Y |
| API-REQ-005 | `/api/v1/requirements/{id}/analyze` | POST | 요구사항 상세 분석 (Event Storming 파이프라인 호출) | Y |
| API-REQ-006 | `/api/v1/requirements/{id}/analysis-view` | GET | 분석 결과 뷰어용 데이터 조회 | Y |

---

## 2. API 상세

### API-REQ-001: 요구사항 등록
- **엔드포인트**: POST `/api/v1/requirements`
- **인증**: Bearer JWT
- **설명**: 마크다운 파일(.md)을 업로드하거나 텍스트를 직접 입력하여 요구사항을 등록한다. (Multipart form-data)
- **요청** (multipart/form-data):
  - `file`: (Optional) 마크다운(.md) 파일, 최대 10MB
  - `data`: (Required, JSON 문자열)
  ```json
  {
    "requirementNo": "REQ-123", // JIRA 티켓명
    "relatedRequirementNo": "REQ-100", // 옵션
    "requirementType": "NEW", // NEW, MOD, DEL
    "projectName": " requirement-clarify-manager",
    "categoryName": "요구사항 관리",
    "requirementName": "요구사항 명세 관리 시스템 구축",
    "authorName": "홍길동",
    "version": "1.0.0",
    "overview": "요구사항에 대한 개략적인 개요...",
    "detail": "수동 입력시 상세 내용 (파일이 없을 경우 필수)"
  }
  ```
- **응답 201 (Created)**:
  ```json
  {
    "id": 1,
    "requirementNo": "REQ-123",
    "status": "Draft",
    "createdAt": "2026-03-26T10:00:00Z"
  }
  ```
- **에러 코드**:
  - `ERR-FILE-001`: 10MB 초과 파일 업로드 시도
  - `ERR-FILE-002`: 지원하지 않는 확장자 업로드 시도
  - `ERR-VAL-001`: 필수 항목 누락
  - `ERR-VAL-002`: 비정상적인 버전 포맷
  - `ERR-DUP-001`: 중복된 요구사항 번호
  - `ERR-REF-001`: 연관 요구사항 번호 부재 경고

### API-REQ-002: 요구사항 상태 변경
- **엔드포인트**: PATCH `/api/v1/requirements/{id}/status`
- **인증**: Bearer JWT
- **요청**:
  ```json
  {
    "status": "Clarifying" // Draft -> Clarifying -> Clarified -> In Progress -> Done
  }
  ```
- **응답 200 (OK)**:
  ```json
  {
    "id": 1,
    "status": "Clarifying",
    "updatedAt": "2026-03-26T10:05:00Z"
  }
  ```
- **에러 코드**:
  - `ERR-STS-001`: 정의되지 않은 상태값 전이 시도

### API-REQ-003: 데이터 사전 자동 생성
- **엔드포인트**: POST `/api/v1/requirements/{id}/data-dictionary/extract`
- **인증**: Bearer JWT
- **설명**: 요구사항 상세 내용을 분석하여 명사/동사를 추출해 프로젝트 단위로 데이터 사전 초안을 생성한다. 비동기 처리 권장.
- **요청**:
  ```json
  {} // 별도 Body 없음, id로 요구사항 텍스트 탐색
  ```
- **응답 202 (Accepted)**:
  ```json
  {
    "jobId": "job-12345",
    "status": "PROCESSING",
    "message": "데이터 사전 추출 작업이 시작되었습니다."
  }
  ```
- **에러 코드**:
  - `ERR-EXT-001`: 명사/동사 추출 엔진 연동 타임아웃/실패

### API-REQ-004: 누락(미정의) 용어 단건 조회
- **엔드포인트**: GET `/api/v1/projects/{projectName}/data-dictionary/pending`
- **인증**: Bearer JWT
- **설명**: 프로젝트 내 미정의 상태인 데이터 사전 용어 중 다음 명확화 대상을 1건씩 반환한다. 모든 용어가 정의되었을 경우 빈 객체를 반환한다.
- **요청**: Query Parameter (None)
- **응답 200 (OK)**:
  ```json
  {
    "termId": 101,
    "koreanName": "작성자ID",
    "isComplete": false, // 더 이상 pending이 없으면 true
    "remainingCount": 5 // 남은 미정의 용어 개수
  }
  ```

### API-REQ-007: 단일 누락 용어 질의 피드백
- **엔드포인트**: POST `/api/v1/projects/{projectName}/data-dictionary/{termId}/clarify`
- **인증**: Bearer JWT
- **설명**: 기획자가 개별 필드(용어) 단위로 명확화된 데이터를 제출한다. 응답으로 즉시 다음 미정의 단어를 반환하여 끊김 없는 인터랙션을 제공한다.
- **요청**:
  ```json
  {
    "action": "DEFINE", // DEFINE, SKIP
    "version": 1, // 동시성 제어용 Optimistic Lock 버전
    "englishName": "authorId",
    "dataType": "VARCHAR",
    "dataLength": 50,
    "description": "요구사항을 작성한 사람의 고유 식별자"
  }
  ```
- **응답 200 (OK)**:
  ```json
  {
    "updated": true,
    "nextPendingTerm": { // 연속적인 입력을 위해 다음 용어 즉시 반환
      "termId": 102,
      "koreanName": "결제금액",
      "isComplete": false,
      "remainingCount": 4
    }
  }
  ```
- **에러 코드**:
  - `ERR-CON-001`: 2명의 기획자가 동시에 동일 용어 정의 시도(낙관적 락)
  - `ERR-NOT-FOUND`: 존재하지 않는 용어 ID

### API-REQ-005: 요구사항 상세 분석 (Event Storming)
- **엔드포인트**: POST `/api/v1/requirements/{id}/analyze`
- **인증**: Bearer JWT
- **설명**: 완료된 데이터 사전과 요구사항 상세 텍스트를 바탕으로 Event Storming 데이터를 추출한다.
- **요청**:
  ```json
  {}
  ```
- **응답 202 (Accepted)**:
  ```json
  {
    "jobId": "job-67890",
    "status": "PROCESSING",
    "message": "Event Storming 분석 파이프라인이 시작되었습니다."
  }
  ```
- **에러 코드**:
  - `ERR-FLOW-001`: 데이터 사전 정의 미완료 상태에서 분석 파이프라인 호출 시도
  - `ERR-EXT-001`: NLP/AI 엔진 타임아웃/실패

### API-REQ-006: 분석 결과 뷰어용 데이터 조회
- **엔드포인트**: GET `/api/v1/requirements/{id}/analysis-view`
- **인증**: Bearer JWT
- **요청**: Query Parameter (None)
- **응답 200 (OK)**:
  ```json
  {
    "requirement": {
      "requirementNo": "REQ-123",
      "originalFileUrl": "/files/.../req-123.md"
    },
    "dataDictionary": [ ... ],
    "eventStorming": {
      "actors": ["기획자"],
      "domainEvents": ["RequirementsCreated"],
      "commands": ["CreateRequirement"],
      "domainObjects": ["Requirement", "DataDictionary"],
      "aggregates": ["RequirementAggregate"],
      "externalSystems": ["JIRA"]
    },
    "diagrams": {
      "businessProcess": "mermaid sequence...",
      "sequenceDiagram": "mermaid sequence...",
      "usecaseDiagram": "mermaid usecase..."
    }
  }
  ```
