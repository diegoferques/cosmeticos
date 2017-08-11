package com.cosmeticos.commons;

import com.cosmeticos.model.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Data
public class CategoryResponseBody {

    private String description;

    private List<Category> serviceList = new ArrayList<>(10);


}
