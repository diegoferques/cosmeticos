package com.cosmeticos.controller;

import com.cosmeticos.commons.LocationRequestBody;
import com.cosmeticos.commons.LocationResponseBody;
import com.cosmeticos.model.Location;
import com.cosmeticos.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.*;

/**
 * Created by matto on 10/07/2017.
 */
@Slf4j
@RestController
public class LocationController {
    @Autowired
    private LocationService locationService;

    @RequestMapping(path = "/locations", method = RequestMethod.GET)
    public HttpEntity<LocationResponseBody> findLastest10() {

        try {
            List<Location> locationList = locationService.find10Lastest();

            LocationResponseBody responseBody = new LocationResponseBody();
            responseBody.setLocationList(locationList);
            responseBody.setDescription("TOP 10 Locations successfully retrieved.");

            log.info("{} Locations successfully retrieved.", locationList.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            LocationResponseBody response = new LocationResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Customer: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = "/locations/{idLocation}", method = RequestMethod.GET)
    public HttpEntity<LocationResponseBody> findById(@PathVariable String idLocation) {

        try {

            Optional<Location> location = locationService.find(Long.valueOf(idLocation));

            if (location.isPresent()) {
                log.info("Busca de LocationGoogle com exito: [{}]", location.get());
                LocationResponseBody response = new LocationResponseBody(location.get());

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", idLocation);
                return notFound().build();
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            LocationResponseBody response = new LocationResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição de LocationGoogle: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = "/locations", method = RequestMethod.POST)
    public HttpEntity<LocationResponseBody> create(@Valid @RequestBody LocationRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Location location = locationService.create(request);
                log.info("Customer adicionado com sucesso:  [{}]", location);
                //return ok().build();
                return ok(new LocationResponseBody(location));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            LocationResponseBody b = new LocationResponseBody();
            b.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }
    }
    @RequestMapping(path = "/locations", method = RequestMethod.PUT)
    public HttpEntity<LocationResponseBody> update(@Valid @RequestBody LocationRequestBody request, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else
            {
                Location location = locationService.update(request);

                LocationResponseBody responseBody = new LocationResponseBody(location);
                log.info("LocationGoogle atualizado com sucesso:  [{}] responseJson[{}]",
                        location,
                        new ObjectMapper().writeValueAsString(responseBody));
                return ok(responseBody);
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            LocationResponseBody response = new LocationResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do LocationGoogle: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(path = "/locations/{idLocation}", method = RequestMethod.DELETE)
    public HttpEntity<LocationResponseBody> delete(@PathVariable String idLocation) {

        String errorCode = String.valueOf(System.nanoTime());

        LocationResponseBody response = new LocationResponseBody();
        response.setDescription("Ação não permitida: Não deletamos registros: " + errorCode);

        log.warn("Ação não permitida para deletar o LocationGoogle: {}. - {}", idLocation, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    private LocationResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        LocationResponseBody responseBody = new LocationResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }
}
