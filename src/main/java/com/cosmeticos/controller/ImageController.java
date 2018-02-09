package com.cosmeticos.controller;

import com.cosmeticos.commons.AddressResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.service.AddressService;
import com.cosmeticos.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
}
