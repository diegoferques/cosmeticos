package com.cosmeticos.commons;

import com.cosmeticos.payment.superpay.client.rest.model.Campainha;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 10/08/2017.
 */
@Data
public class CampainhaSuperpeyResponseBody {
    private String description;

    private List<Campainha> campainha = new ArrayList<>(10);

    public CampainhaSuperpeyResponseBody() {
    }

    public CampainhaSuperpeyResponseBody(Campainha campainha) {
        this.campainha.add(campainha);
    }
}
