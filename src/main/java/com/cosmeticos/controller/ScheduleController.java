package com.cosmeticos.controller;


import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class ScheduleController {

    private @Autowired ScheduleService service;

    @RequestMapping(path = "/schedule", method = RequestMethod.POST)
    public HttpEntity<ScheduleResponseBody> create(@Valid @RequestBody ScheduleRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Schedule s = service.create(request);
            log.info("Schedule adicionado com sucesso:  [{}]", s);
            return ok().build();
        }
    }

    private ScheduleResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        ScheduleResponseBody responseBody = new ScheduleResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }


}
