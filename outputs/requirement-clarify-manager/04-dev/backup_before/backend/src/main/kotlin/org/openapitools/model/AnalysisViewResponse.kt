package org.openapitools.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonProperty
import org.openapitools.model.AnalysisViewResponseDiagrams
import org.openapitools.model.AnalysisViewResponseEventStorming
import org.openapitools.model.AnalysisViewResponseRequirement
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
 * @param requirement 
 * @param dataDictionary 
 * @param eventStorming 
 * @param diagrams 
 */
data class AnalysisViewResponse(

    @field:Valid
    @Schema(example = "null", description = "")
    @get:JsonProperty("requirement") val requirement: AnalysisViewResponseRequirement? = null,

    @field:Valid
    @Schema(example = "null", description = "")
    @get:JsonProperty("dataDictionary") val dataDictionary: kotlin.collections.List<kotlin.Any>? = null,

    @field:Valid
    @Schema(example = "null", description = "")
    @get:JsonProperty("eventStorming") val eventStorming: AnalysisViewResponseEventStorming? = null,

    @field:Valid
    @Schema(example = "null", description = "")
    @get:JsonProperty("diagrams") val diagrams: AnalysisViewResponseDiagrams? = null
    ) {

}

