package com.cosmeticos.commons;

import com.cosmeticos.model.Customer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class CustomerResponseBody {

    private String description;

    private List<Customer> customerList = new ArrayList<>(10);

    public CustomerResponseBody() {
    }

    public CustomerResponseBody(Customer customer) {
        this.customerList.add(customer);
    }
}
