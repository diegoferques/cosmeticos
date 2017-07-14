
package com.cosmeticos.commons.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationGoogle {

    @JsonProperty("lat")
    public Double lat;
    @JsonProperty("lng")
    public Double lng;

}
