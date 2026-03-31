package org.openapitools.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 
 * @param businessProcess 
 * @param sequenceDiagram 
 * @param usecaseDiagram 
 */
data class AnalysisViewResponseDiagrams(

    @Schema(example = "null", description = "")
    @get:JsonProperty("businessProcess") val businessProcess: kotlin.String? = null,

    @Schema(example = "null", description = "")
    @get:JsonProperty("sequenceDiagram") val sequenceDiagram: kotlin.String? = null,

    @Schema(example = "null", description = "")
    @get:JsonProperty("usecaseDiagram") val usecaseDiagram: kotlin.String? = null
    ) {

}

