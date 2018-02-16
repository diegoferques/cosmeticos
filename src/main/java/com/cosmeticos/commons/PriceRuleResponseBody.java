package com.cosmeticos.commons;

import com.cosmeticos.model.Customer;
import com.cosmeticos.model.PriceRule;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PriceRuleResponseBody {

    private String description;

    private List<PriceRule> customerList = new ArrayList<>(10);

    public PriceRuleResponseBody() {
    }

    public PriceRuleResponseBody(PriceRule customer) {
        this.customerList.add(customer);
    }
}
