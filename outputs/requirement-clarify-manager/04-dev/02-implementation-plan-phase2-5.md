# Implementation Plan: Requirement Clarify Manager (TC-013 ~ TC-036)

This plan outlines the development steps to implement the remaining test cases, ensuring a robust and layered Spring Boot architecture.

## 📅 Phase 2: Status Workflow & Transition (TC-013 ~ TC-019)
**Goal:** Implement a strictly controlled state machine for requirements.

### 1. Model & Repository Updates
- **RequirementEntity**: Add `updatedAt` and current status.
- **RequirementHistoryEntity**: Create a new entity to log status changes (Who, When, From, To).
- **RequirementStatus**: Define as a Kotlin Enum (DRAFT, CLARIFYING, CLARIFIED, IN_PROGRESS, DONE).

### 2. Service Logic
- Implement `RequirementService.updateStatus(id, newStatus)`.
- **Validation**: 
  - Valid transitions only: `DRAFT -> CLARIFYING -> CLARIFIED -> IN_PROGRESS -> DONE`.
  - Block "Done" requirement modifications (TC-019).
  - Throw `InvalidStatusTransitionException` (mapped to `ERR-STS-001`) for invalid leaps (TC-017, TC-018).

### 3. Controller
- Update `ApiImpl.apiV1RequirementsIdStatusPatch`.

---

## 📅 Phase 3: Data Dictionary Extraction & Management (TC-020 ~ TC-027)
**Goal:** Tokenize markdown content and manage interactive clarification.

### 1. New Entities
- **DataDictionaryEntity**: 
  - `projectName`, `koreanName`, `englishName`, `description`, `status` (PENDING, DEFINED, SKIPPED).
  - `version: Long` for Optimistic Locking (TC-026).
- **SynonymMap**: Basic table/map to group similar terms (TC-022).

### 2. Extraction Service (Backend Worker)
- **Regex/NLP Parser**: Extract nouns from `originalContent`.
- **Project Isolation**: Ensure terms are scoped by `projectName` (TC-021).
- **Async Handling**: Return a `jobId` immediately and process items in `@Async` thread or using a TaskExecutor.

### 3. Clarification API
- `getPendingTerm`: Select 1 term where status is `PENDING`.
- `clarifyTerm`: Update term with `englishName`, `dataType`, etc. Support `SKIP` action (TC-025).
- **Security & Validation**: Prevent cross-project access (TC-034).

---

## 📅 Phase 4: Event Storming Analysis (TC-028 ~ TC-031)
**Goal:** Map defined terms and requirements into a structured JSON model.

### 1. Precondition Check
- Verify `Requirement.status == CLARIFIED` and all associated `DataDictionary` items for the requirement are `DEFINED` (TC-029).

### 2. Analysis Engine (Mockable Interface)
- Implement `AnalysisService.analyze(requirementId)`.
- Extract **Actors**, **Commands**, **Events** from the text and map them to defined dictionary terms (TC-030).
- Store result as a JSON/CLOB in `AnalysisResultEntity`.

### 3. Job Monitoring
- Implement a basic status check for the Analysis job (TC-031).

---

## 📅 Phase 5: Results Viewer & Visualization (TC-032 ~ TC-036)
**Goal:** Serve visualization data (Mermaid.js compatible).

### 1. Viewer API
- `getAnalysisView`: Aggregates Requirement content, Data Dictionary list, and Event Storming JSON.
- Generate **Mermaid.js** strings for Sequence and Usecase diagrams (TC-035).

### 2. Performance & Export
- Ensure raw MD content is served for lazy loading (TC-033).
- Implement basic PNG/PDF export redirect or placeholder logic (TC-036).

---

## 🛠 Required Tasks & Sequence

1.  **Migration**: Add new tables (`data_dictionary`, `analysis_results`, `status_history`).
2.  **Phase 2**: Implement State Machine with Unit Tests.
3.  **Phase 3**: Implement Extraction Logic & Pending Term Queue.
4.  **Phase 4**: Implement Analysis Precondition & Model Mapping.
5.  **Phase 5**: Implement Visualization Data Aggregator.

**Review requested on:**
- State transition rules (Are they too strict?).
- Project isolation strategy (Scoped by `projectName` in URL).
- Async processing approach for MVP.
