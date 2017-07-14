
package com.cosmeticos.commons.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddressComponent {

    @JsonProperty("long_name")
    public String longName;
    @JsonProperty("short_name")
    public String shortName;
    @JsonProperty("types")
    public List<String> types = null;

}
