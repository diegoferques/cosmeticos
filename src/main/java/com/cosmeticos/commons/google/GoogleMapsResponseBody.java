
package com.cosmeticos.commons.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoogleMapsResponseBody {

    @JsonProperty("results")
    public List<Result> results = null;
    @JsonProperty("status")
    public String status;

}
