# 시퀀스 다이어그램

## 1. 요구사항 등록 및 데이터 사전 자동 생성 흐름
요구사항이 업로드 된 직후, 비동기 파이프라인을 통해 데이터 사전 단어가 추출되는 과정입니다.

```mermaid
sequenceDiagram
    autonumber
    actor 기획자
    participant WebClient
    participant ApiServer
    participant Database
    participant NLP_Engine as AI/NLP Engine

    기획자->>WebClient: 새 요구사항 입력 (파일/텍스트)
    WebClient->>ApiServer: POST /api/v1/requirements
    
    ApiServer->>Database: requirement_no 중복 체크
    Database-->>ApiServer: OK
    ApiServer->>Database: 항목 저장 (Status: Draft, Detail: LOB)
    Database-->>ApiServer: 저장 완료

    %% 비동기 데이터 사전 추출 시작
    ApiServer->>NLP_Engine: 데이터 분석 및 단어 추출 비동기 큐 전송
    ApiServer-->>WebClient: 201 Created 반환
    WebClient-->>기획자: "저장 완료 및 추출 중" 표시

    note right of NLP_Engine: 비동기 처리
    NLP_Engine->>NLP_Engine: 형태소 분석 및 명사/동사 추출
    NLP_Engine->>ApiServer: 추출 결과 Webhook / Callback 전송
    ApiServer->>Database: 기존 Data Dictionary 데이터와 대조
    ApiServer->>Database: 미정의(Undefine) 항목 Insert
    ApiServer->>WebClient: 분석 완료 SSE/WebSocket 알림
    WebClient-->>기획자: "데이터 사전 추출 완료. 미정의 5건 확인 요망" 알림
```

## 2. 데이터 사전 용어 명확화 흐름 (누락 용어 피드백)
기획자가 시스템이 질의한 미정의 데이터 사전 항목들을 채워넣는 흐름입니다.

```mermaid
sequenceDiagram
    autonumber
    actor 기획자
    participant WebClient
    participant ApiServer
    participant Database

    WebClient->>ApiServer: GET /api/v1/projects/{name}/data-dictionary/pending
    ApiServer->>Database: 미정의 용어 1건 조회
    Database-->>ApiServer: 용어 A 데이터(Term Id: 101) 반환
    ApiServer-->>WebClient: 질문: "용어 A를 정의하시겠습니까?"
    
    loop 남은 미정의 용어가 0이 될 때까지 (또는 사용자 중단 시까지)
        WebClient-->>기획자: 용어 A 필드(영문명, 길이 등) 입력 요청
        기획자->>WebClient: 데이터 딕셔너리 필드 입력 및 확정 클릭 (또는 Skip)
        
        WebClient->>ApiServer: POST /api/v1/projects/{name}/data-dictionary/101/clarify
        ApiServer->>Database: term_id 조회 및 현재 version 확인
        Database-->>ApiServer: 현재 version
        
        alt Action == DEFINE
            ApiServer->>Database: version 조건으로 Update 시도 (Optimistic Lock)
            alt 성공 (version 매칭)
                Database-->>ApiServer: 1 Row Updated
            else 실패 (version 불일치)
                Database-->>ApiServer: 0 Row Updated
                ApiServer-->>WebClient: 409 충돌 발생 알림
            end
        else Action == SKIP
            ApiServer->>ApiServer: 상태 유지
        end
        
        ApiServer->>Database: 다음 미정의 용어 1건(용어 B) 조회
        Database-->>ApiServer: 용어 B 데이터(Term Id: 102) 반환
        ApiServer-->>WebClient: 200 OK + 다음 용어 B 정보 즉시 포함
        WebClient-->>기획자: 용어 A 완료 알림 및 즉시 용어 B 입력란 화면 전환
    end
    
    WebClient-->>기획자: "모든 데이터 사전 정의 완료" 최종 표시
```

## 3. 요구사항 상세 분석 (Event Storming 파이프라인 호출)
정의된 요구사항 상세 텍스트와 데이터 사전을 종합하여 이벤트 스토밍 분석을 실행하는 과정입니다.

```mermaid
sequenceDiagram
    autonumber
    actor 기획자
    participant WebClient
    participant ApiServer
    participant Database
    participant Analysis_Engine as Event Storming Engine

    기획자->>WebClient: "상세 분석 시작" 클릭
    WebClient->>ApiServer: POST /api/v1/requirements/{id}/analyze
    
    ApiServer->>Database: 해당 프로젝트 데이터 사전 미정의 항목 존재 여부 쿼리
    Database-->>ApiServer: 결과 반환
    
    alt 미정의 항목 존재
        ApiServer-->>WebClient: 422 Unprocessable Entity 에러 반환 (사전 정의 미완료)
        WebClient-->>기획자: "먼저 미정의된 데이터 용어를 100% 채워주세요" 메시지
    else 모든 항목 정의됨
        ApiServer->>Database: 요구사항 Detail 내용 및 관련 데이터 사전 로드
        Database-->>ApiServer: 데이터 반환
        ApiServer->>Analysis_Engine: Event Storming 분석 비동기 요청
        ApiServer-->>WebClient: 202 Accepted 반환
        note right of Analysis_Engine: 비동기 처리
        Analysis_Engine->>Analysis_Engine: LLM/AI 기반 문맥 매핑 및 요소 식별
        Analysis_Engine->>ApiServer: 분석 완료 Callback (Actor, Event, Command 등)
        ApiServer->>Database: EVENT_STORMING_NODE 에 결과 적재
        ApiServer->>WebClient: 비동기 알림 전송
        WebClient-->>기획자: "분석 완료 및 시각화 보드 준비" 알림
    end
```
