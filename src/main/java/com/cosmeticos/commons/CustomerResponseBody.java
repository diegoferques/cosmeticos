package com.cosmeticos.commons;

import com.cosmeticos.model.Customer;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 27/05/2017.
 */
@Data
@Builder
@AllArgsConstructor
public class CustomerResponseBody {

    @JsonView(
            {
                    ResponseJsonView.CustomerControllerUpdate.class,
                    ResponseJsonView.CustomerControllerGet.class
            })
    private String description;

    @JsonView(
            {
                    ResponseJsonView.CustomerControllerUpdate.class,
                    ResponseJsonView.CustomerControllerGet.class
            })
    private List<Customer> customerList = new ArrayList<>(10);

    public CustomerResponseBody() {
    }

    public CustomerResponseBody(Customer customer) {
        this.customerList.add(customer);
    }
}
