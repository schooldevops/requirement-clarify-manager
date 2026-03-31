# DataDictionaryApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**apiV1ProjectsProjectNameDataDictionaryPendingGet**](#apiv1projectsprojectnamedatadictionarypendingget) | **GET** /api/v1/projects/{projectName}/data-dictionary/pending | 미정의 용어 단건 조회|
|[**apiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost**](#apiv1projectsprojectnamedatadictionarytermidclarifypost) | **POST** /api/v1/projects/{projectName}/data-dictionary/{termId}/clarify | 단일 용어 명확화 제출 (인터랙션 피드백)|
|[**apiV1RequirementsIdDataDictionaryExtractPost**](#apiv1requirementsiddatadictionaryextractpost) | **POST** /api/v1/requirements/{id}/data-dictionary/extract | 데이터 사전 자동 추출 요청 (비동기)|

# **apiV1ProjectsProjectNameDataDictionaryPendingGet**
> PendingTermResponse apiV1ProjectsProjectNameDataDictionaryPendingGet()

프로젝트 내 미정의(Undefine) 상태인 데이터 사전 용어 중 다음 명확화 대상 1건을 조회합니다.

### Example

```typescript
import {
    DataDictionaryApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new DataDictionaryApi(configuration);

let projectName: string; // (default to undefined)

const { status, data } = await apiInstance.apiV1ProjectsProjectNameDataDictionaryPendingGet(
    projectName
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **projectName** | [**string**] |  | defaults to undefined|


### Return type

**PendingTermResponse**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost**
> ApiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost200Response apiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost(clarifyRequest)

기획자가 특정 용어에 대해 정의(DEFINE)를 수행하거나 건너뛰기(SKIP)를 선택합니다. 응답으로 즉시 다음 미정의 단어를 반환합니다.

### Example

```typescript
import {
    DataDictionaryApi,
    Configuration,
    ClarifyRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new DataDictionaryApi(configuration);

let projectName: string; // (default to undefined)
let termId: number; // (default to undefined)
let clarifyRequest: ClarifyRequest; //

const { status, data } = await apiInstance.apiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost(
    projectName,
    termId,
    clarifyRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **clarifyRequest** | **ClarifyRequest**|  | |
| **projectName** | [**string**] |  | defaults to undefined|
| **termId** | [**number**] |  | defaults to undefined|


### Return type

**ApiV1ProjectsProjectNameDataDictionaryTermIdClarifyPost200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |
|**404** | 존재하지 않는 용어 ID |  -  |
|**409** | 동시성 충돌 (Optimistic Lock 예외) |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apiV1RequirementsIdDataDictionaryExtractPost**
> AsyncJobResponse apiV1RequirementsIdDataDictionaryExtractPost()


### Example

```typescript
import {
    DataDictionaryApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new DataDictionaryApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.apiV1RequirementsIdDataDictionaryExtractPost(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


### Return type

**AsyncJobResponse**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**202** | Accepted (Processing) |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

