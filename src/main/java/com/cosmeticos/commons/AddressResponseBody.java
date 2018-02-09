package com.cosmeticos.commons;

import com.cosmeticos.model.Address;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class AddressResponseBody {

    private String description;

    private List<Address> addressList = new ArrayList<>(10);

}
