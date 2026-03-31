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
 * @param action 
 * @param version Optimistic Lock version
 * @param englishName 
 * @param dataType 
 * @param dataLength 
 * @param description 
 */
data class ClarifyRequest(

    @Schema(example = "null", required = true, description = "")
    @get:JsonProperty("action", required = true) val action: ClarifyRequest.Action,

    @Schema(example = "null", required = true, description = "Optimistic Lock version")
    @get:JsonProperty("version", required = true) val version: kotlin.Int,

    @Schema(example = "null", description = "")
    @get:JsonProperty("englishName") val englishName: kotlin.String? = null,

    @Schema(example = "null", description = "")
    @get:JsonProperty("dataType") val dataType: kotlin.String? = null,

    @Schema(example = "null", description = "")
    @get:JsonProperty("dataLength") val dataLength: kotlin.Int? = null,

    @Schema(example = "null", description = "")
    @get:JsonProperty("description") val description: kotlin.String? = null
    ) {

    /**
    * 
    * Values: DEFINE,SKIP
    */
    enum class Action(@get:JsonValue val value: kotlin.String) {

        DEFINE("DEFINE"),
        SKIP("SKIP");

        companion object {
            @JvmStatic
            @JsonCreator
            fun forValue(value: kotlin.String): Action {
                return values().first{it -> it.value == value}
            }
        }
    }

}

