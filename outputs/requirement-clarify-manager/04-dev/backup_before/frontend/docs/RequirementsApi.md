# RequirementsApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**apiV1RequirementsIdStatusPatch**](#apiv1requirementsidstatuspatch) | **PATCH** /api/v1/requirements/{id}/status | 요구사항 상태 변경|
|[**apiV1RequirementsPost**](#apiv1requirementspost) | **POST** /api/v1/requirements | 요구사항 등록|

# **apiV1RequirementsIdStatusPatch**
> ApiV1RequirementsIdStatusPatch200Response apiV1RequirementsIdStatusPatch(apiV1RequirementsIdStatusPatchRequest)


### Example

```typescript
import {
    RequirementsApi,
    Configuration,
    ApiV1RequirementsIdStatusPatchRequest
} from './api';

const configuration = new Configuration();
const apiInstance = new RequirementsApi(configuration);

let id: number; // (default to undefined)
let apiV1RequirementsIdStatusPatchRequest: ApiV1RequirementsIdStatusPatchRequest; //

const { status, data } = await apiInstance.apiV1RequirementsIdStatusPatch(
    id,
    apiV1RequirementsIdStatusPatchRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **apiV1RequirementsIdStatusPatchRequest** | **ApiV1RequirementsIdStatusPatchRequest**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**ApiV1RequirementsIdStatusPatch200Response**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | OK |  -  |
|**400** | Bad Request (유효성 검증 실패 등) |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **apiV1RequirementsPost**
> RequirementResponse apiV1RequirementsPost()

마크다운 파일(.md)을 업로드하거나 텍스트를 직접 입력하여 요구사항을 등록합니다.

### Example

```typescript
import {
    RequirementsApi,
    Configuration
} from './api';

const configuration = new Configuration();
const apiInstance = new RequirementsApi(configuration);

let data: string; //요구사항 메타데이터 및 상세 텍스트 (JSON 문자열) (default to undefined)
let file: File; //마크다운(.md) 파일 (최대 10MB) (optional) (default to undefined)

const { status, data } = await apiInstance.apiV1RequirementsPost(
    data,
    file
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **data** | [**string**] | 요구사항 메타데이터 및 상세 텍스트 (JSON 문자열) | defaults to undefined|
| **file** | [**File**] | 마크다운(.md) 파일 (최대 10MB) | (optional) defaults to undefined|


### Return type

**RequirementResponse**

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | Created |  -  |
|**400** | Bad Request (유효성 검증 실패 등) |  -  |
|**413** | Payload Too Large (10MB 초과) |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

