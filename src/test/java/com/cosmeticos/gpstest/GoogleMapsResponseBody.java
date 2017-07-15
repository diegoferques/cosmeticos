
package com.cosmeticos.gpstest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class GoogleMapsResponseBody {

    @JsonProperty("results")
    public List<Result> results = null;
    @JsonProperty("status")
    public String status;

}
