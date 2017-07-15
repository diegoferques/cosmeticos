
package com.cosmeticos.commons.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Geometry {

    @JsonProperty("location")
    public LocationGoogle location;
    @JsonProperty("location_type")
    public String locationType;
}
