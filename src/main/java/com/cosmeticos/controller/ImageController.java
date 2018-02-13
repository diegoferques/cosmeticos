package com.cosmeticos.controller;

import com.cosmeticos.commons.AddressResponseBody;
import com.cosmeticos.commons.CategoryResponseBody;
import com.cosmeticos.commons.ImageResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Category;
import com.cosmeticos.model.Image;
import com.cosmeticos.service.AddressService;
import com.cosmeticos.service.ImageService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;

    @RequestMapping(path = "/images/{id}", method = RequestMethod.DELETE)
    public HttpEntity<Void> delete(
            @PathVariable("id") Long id
    ) {

        try {

            imageService.delete(id);

            log.info("Image {} deletada com sucesso.", id);

            return ok().build();

        } catch (Exception e) {
            log.error("Failed to retrieve ProfessionalServices: {}", e.getMessage(), e);

            return ResponseEntity.status(500).build();
        }

    }


    @JsonView(ResponseJsonView.ImageGetAll.class)
    @RequestMapping(path = "/images", method = RequestMethod.GET)
    public HttpEntity<ImageResponseBody> findAll(@ModelAttribute Image image) {

        try {

            List<Image> entityList = imageService.findAll(image);

            ImageResponseBody responseBody = new ImageResponseBody();
            responseBody.setImageList(entityList);
            responseBody.setDescription("All Services retrieved.");

            log.info("{} Services successfully retrieved.", entityList.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            ImageResponseBody responseBody = new ImageResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }


}
