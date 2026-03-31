# 용어 정의서 (Data Dictionary)

`01-pm-agent.md`의 도메인 용어 표준화 규정과 `001-requirement-mvp.md`의 명확화(Clarify) 요구사항을 모두 반영하여 작성된 도메인 용어 사전입니다.
기획자에게 역으로 질의해야 하는 "명확화 상태(Clarify Status)"와 "명확화 질의(Clarify Question)" 항목을 새롭게 포함했습니다.

## 1. 요구사항 메타데이터 (Requirement Metadata)

| 한글이름 | 영문이름 | 단축영문이름 | 동음이의어 / 이음동의어 | 값 범위 및 제약조건 | 설명 | 명확화 상태 (Clarify Status) | 명확화 질의 (Clarify Question) |
|---|---|---|---|---|---|---|---|
| 요구사항 번호 | Requirement ID | reqId | 티켓 번호, 이슈 번호 | 고유키 (Unique) | 프로젝트 내 요구사항을 식별하는 고유 키 | Defined | - |
| 연관 요구사항 번호 | Related Req ID | relatedReqId | 참조 요구사항 | - | 이전 또는 관련 요구사항과의 연결 고리 | Defined | - |
| 신규 구분 | Requirement Type | reqType | 신규여부, 수정구분 | `신규`, `수정` | 요구사항이 완전 신규인지 기존 기능의 수정인지 구분 | Defined | - |
| 프로젝트 명 | Project Name | projName | 서비스 명, 시스템 명 | - | 요구사항 및 데이터 사전 격리의 기준 단위 | Defined | - |
| 카테고리명 | Task Category | taskCategory | 업무 카테고리 | `회원`,`결제`,`정산`,`배송`,`할인`,`상품`,`기타` | 프로젝트 내의 업무 범주 분류 | Defined | - |
| 요구사항 이름 | Requirement Name | reqName | 요구사항 제목 | Not Null | 요구사항에 대한 핵심 개요 문장/제목 | Defined | - |
| 작성자 | Author | author | 작성자명, 작성자 사번 | Not Null | 요구사항 작성자의 식별 정보 | Defined | - |
| 작성일 | Created At | createdAt | 등록일, 생성일 | `YYYY-MM-DD HH:mm:ss` | 요구사항이 처음 등록된 일시 | Defined | - |
| 수정일 | Modified At | modifiedAt | 갱신일, 변경일 | `YYYY-MM-DD HH:mm:ss` | 요구사항이 마지막으로 수정된 일시 | Defined | - |
| 버전 | Version | version | 리비전 | `Major-Minor-Patch` | 요구사항 문서의 리비전 상태 | Defined | - |
| 요구사항 개요 | Req Overview | reqOverview | 요약 | Max 4000자 | 요구사항 전반의 간략한 설명 | Defined | - |
| 요구사항 상세 | Req Detail | reqDetail | 본문 | 최대 수 MB 텍스트 | 대량의 텍스트로 구성된 요구사항의 본문 내용 | Defined | - |
| 요구사항 상태 | Req Status | reqStatus | 진행 상태 | `Draft`, `Clarifying`, `Clarified`, `In Progress`, `Done` | 요구사항의 워크플로우 진행 단계 표시 | Defined | - |

## 2. 데이터 사전 및 명확화 모듈 (Data Dictionary & Clarification)

| 한글이름 | 영문이름 | 단축영문이름 | 동음이의어 / 이음동의어 | 값 범위 및 제약조건 | 설명 | 명확화 상태 (Clarify Status) | 명확화 질의 (Clarify Question) |
|---|---|---|---|---|---|---|---|
| 데이터 사전 | Data Dictionary | dataDict | 용어 집합, 단어장 | - | 명사/동사로 자동 추출된 프로젝트 기반 용어 모음 | Defined | - |
| 추출 용어 | Extracted Term | extTerm | 어휘, 추출 단어 | - | 요구사항 상세 텍스트에서 추출된 명사/동사 | Defined | - |
| 피드백 질의 | Feedback Query | feedbackQuery | 명확화 요청 | Max 1000자 | 미정의된 용어에 대해 기획자에게 정의를 요구하는 텍스트 | Defined | - |
| 용어 정의 상태 | Term Status | termStatus | - | `Defined`, `Undefined`, `Skipped` | 개별 추출 용어의 사전 정의 완료 여부 | Defined | - |
| 동시성 제어 키 | Optimistic Lock | optLock | - | 순차 증가 Integer | 다수 기획자가 동시에 동일 용어 정의 시도 시 충돌 제어 | Defined | - |

## 3. 이벤트 스토밍 및 분석 산출물 (Event Storming & Views)

| 한글이름 | 영문이름 | 단축영문이름 | 동음이의어 / 이음동의어 | 값 범위 및 제약조건 | 설명 | 명확화 상태 (Clarify Status) | 명확화 질의 (Clarify Question) |
|---|---|---|---|---|---|---|---|
| 이벤트 스토밍 | Event Storming | evtStorming | - | 분석 파이프라인 | 액터, 이벤트 등을 추출해 내는 분석 이벤트 | Defined | - |
| 액터 | Actor | actor | 사용자, 행위자 | - | 시스템을 사용하거나 이벤트를 촉발하는 주체 | Defined | - |
| 커맨드 | Command | command | 명령 | 동사형 텍스트 | 사용자 및 내부 스케줄러가 시스템에 지시하는 의도된 동작 | Defined | - |
| 도메인 이벤트 | Domain Event | domainEvent | 상태 변경 이벤트 | 과거형 텍스트 | 커맨드의 결과로 도메인 객체에 반영된 상태 및 사실 | Defined | - |
| 도메인 객체 | Domain Object | domainObj | 엔티티, 모델 | 명사형 텍스트 | 이벤트가 발생하고 상태를 관리하는 핵심 도메인 정보 | Defined | - |
| 애그리거트 | Aggregate | aggregate | 트랜잭션 경계 | - | 관련된 도메인 객체들을 하나의 트랜잭션 단위로 묶은 군집 | Defined | - |
| 외부 시스템 | External System | extSystem | 서드파티 | - | 시스템 바운더리 외부에 위치한 연동 시스템 | Needs Clarification | 외부 시스템의 연동 실패 시의 보상 트랜잭션(Saga) 설계까지 산출물로 제공해야 하나요? |
| 시각화 뷰어 | Visual Viewer | visualViewer | 분석 뷰 | - | 이벤트스토밍, 다이어그램 등을 보여주는 웹 UI 컴포넌트 | Defined | - |
