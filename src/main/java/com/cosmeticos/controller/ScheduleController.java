package com.cosmeticos.controller;


import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import com.cosmeticos.commons.ScheduleResponseBody;
import com.cosmeticos.commons.ScheduleRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ScheduleController {

    @RequestMapping(path = "/schedule", method = RequestMethod.POST)
    public HttpEntity<ScheduleResponseBody> create(@Valid @RequestBody ScheduleRequestBody request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        }
        else
        {
            log.info("Schedule adicionado com sucesso scheduledDate[{}], ownerUser[{}]" //
                , request.getScheduleDate().toString()
                , request.getOwnerUser());
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
