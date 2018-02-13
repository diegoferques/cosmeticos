package com.cosmeticos.commons;

import com.cosmeticos.model.Image;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageResponseBody {

    private String description;

    private List<Image> imageList = new ArrayList<>();

    public ImageResponseBody(Image image) {
        this.imageList.add(image);
    }

    public ImageResponseBody() {

    }
}
