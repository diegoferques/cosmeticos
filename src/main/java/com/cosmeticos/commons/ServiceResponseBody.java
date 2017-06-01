package com.cosmeticos.commons;

import com.cosmeticos.model.Service;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Data
public class ServiceResponseBody {

    private String description;

    private List<Service> serviceList = new ArrayList<>(10);

    public ServiceResponseBody() {
    }

    public ServiceResponseBody(Service service) {
        this.serviceList.add(service);
    }
}
