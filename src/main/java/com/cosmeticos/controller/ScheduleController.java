package com.cosmeticos.controller;


import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class ScheduleController {

    private @Autowired ScheduleService service;

    @RequestMapping(path = "/schedules", method = RequestMethod.POST)
    public HttpEntity<ScheduleResponseBody> create(@Valid @RequestBody ScheduleRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Schedule s = service.create(request);
            log.info("Schedule adicionado com sucesso:  [{}]", s);
            return ok().       build();
        }
    }

    @RequestMapping(path = "/schedules", method = RequestMethod.PUT)
    public HttpEntity<ScheduleResponseBody> update(@Valid @RequestBody ScheduleRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            Schedule s = service.update(request);
            log.info("Schedule atualizado com sucesso:  [{}]", s);
            return ok(new ScheduleResponseBody(s));
        }
    }

    @RequestMapping(path = "/schedules/{id}", method = RequestMethod.GET)
    public HttpEntity<ScheduleResponseBody> findById(@PathVariable String id) {

        Optional<Schedule> s = service.find(Long.valueOf(id));

        if(s.isPresent()) {
            log.info("Busca de Schedule com exito: [{}]", s.get());
            ScheduleResponseBody response = new ScheduleResponseBody(s.get());

            return ok().body(response);
        }
        else
        {
            log.error("Nenhum registro encontrado para o id: {}", id);
            return notFound().build();
        }
    }

    @RequestMapping(path = "/schedules", method = RequestMethod.GET)
    public HttpEntity<ScheduleResponseBody> findLastest10() {

         List<Schedule> entitylist = service.find10Lastest();

        ScheduleResponseBody responseBody = new ScheduleResponseBody();
        responseBody.setScheduleList(entitylist);
        responseBody.setDescription("TOP 10 successfully retrieved.");

        log.info("{} schedules successfully retrieved.", entitylist.size());

        return ok().body(responseBody);
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
