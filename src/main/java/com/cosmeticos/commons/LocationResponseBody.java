package com.cosmeticos.commons;

import com.cosmeticos.model.Location;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 10/07/2017.
 */
@Data
public class LocationResponseBody {
    private String description;

    private List<Location> locationList = new ArrayList<>(10);

    public LocationResponseBody() {
    }

    public LocationResponseBody(Location location) {
        this.locationList.add(location);
    }
}
