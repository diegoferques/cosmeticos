
package com.cosmeticos.gpstest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Location {

    @JsonProperty("lat")
    public Double lat;
    @JsonProperty("lng")
    public Double lng;

}
