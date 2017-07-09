
package com.cosmeticos.gpstest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Result {

    // Comentado pq traz muita informacao irrelevante.
    //@JsonProperty("address_components")
    //public List<AddressComponent> addressComponents = null;

    @JsonProperty("formatted_address")
    public String formattedAddress;
    @JsonProperty("geometry")
    public Geometry geometry;
    @JsonProperty("partial_match")
    public Boolean partialMatch;
    @JsonProperty("place_id")
    public String placeId;
    @JsonProperty("types")
    public List<String> types = null;

}
