package com.cosmeticos.commons;

import com.cosmeticos.model.Hability;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lulu on 21/06/2017.
 */
@Data
public class HabilityResponseBody {

    private String description;

    private List<Hability> habilityList = new ArrayList<>();

    public HabilityResponseBody(Hability customer) {
        this.habilityList.add(customer);
    }

    public HabilityResponseBody() {

    }
}
