# AnalysisApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**apiV1RequirementsIdAnalysisViewGet**](#apiv1requirementsidanalysisviewget) | **GET** /api/v1/requirements/{id}/analysis-view | 통합 분석 결과 뷰어 조회|
|[**apiV1RequirementsIdAnalyzePost**](#apiv1requirementsidanalyzepost) | **POST** /api/v1/requirements/{id}/analyze | 요구사항 상세 Event Storming 분석 요청 (비동기)|

# **apiV1RequirementsIdAnalysisViewGet**
> AnalysisViewResponse apiV1RequirementsIdAnalysisViewGet()


### Example

```typescript
import {
    AnalysisApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AnalysisApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.apiV1RequirementsIdAnalysisViewGet(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


### Return type

**AnalysisViewResponse**

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

# **apiV1RequirementsIdAnalyzePost**
> AsyncJobResponse apiV1RequirementsIdAnalyzePost()

완성된 데이터 사전을 바탕으로 Event Storming 요소들을 추출합니다.

### Example

```typescript
import {
    AnalysisApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new AnalysisApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.apiV1RequirementsIdAnalyzePost(
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
|**422** | Unprocessable Entity (데이터 사전 미완료) |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

