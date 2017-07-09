
package com.cosmeticos.gpstest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Geometry {

    @JsonProperty("location")
    public Location location;
    @JsonProperty("location_type")
    public String locationType;
}
