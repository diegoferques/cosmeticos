
package com.cosmeticos.gpstest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class AddressComponent {

    @JsonProperty("long_name")
    public String longName;
    @JsonProperty("short_name")
    public String shortName;
    @JsonProperty("types")
    public List<String> types = null;

}
