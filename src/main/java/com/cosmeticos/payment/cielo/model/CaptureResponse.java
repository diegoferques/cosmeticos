
package com.cosmeticos.payment.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CaptureResponse {

    @JsonProperty("Status")
    public Integer status;

    @JsonProperty("ReturnCode")
    public String returnCode;

    @JsonProperty("ReturnMessage")
    public String returnMessage;

    @JsonProperty("Links")
    public List<ResponseLink> links = null;

}
