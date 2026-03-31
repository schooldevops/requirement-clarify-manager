package org.openapitools.model

import java.util.Objects
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
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
 * @param status 
 */
data class ApiV1RequirementsIdStatusPatchRequest(

    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("status", required = true) val status: ApiV1RequirementsIdStatusPatchRequest.Status
    ) {

    /**
    * 
    * Values: Draft,Clarifying,Clarified,In_Progress,Done
    */
    enum class Status(@get:JsonValue val value: kotlin.String) {

        Draft("Draft"),
        Clarifying("Clarifying"),
        Clarified("Clarified"),
        In_Progress("In Progress"),
        Done("Done");

        companion object {
            @JvmStatic
            @JsonCreator
            fun forValue(value: kotlin.String): Status {
                return values().first{it -> it.value == value}
            }
        }
    }

}

