package com.cosmeticos.subscription.product;

import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.commons.RoleResponseBody;
import com.cosmeticos.model.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by Vinicius on 17/07/2017.
 */
@Slf4j
@RestController
public class ProductController {

    @Autowired
    private ProductService service;

    @RequestMapping(path = "/products", method = RequestMethod.POST)
    public HttpEntity<ProductResponseBody> create (@Valid @RequestBody ProductRequestBody request, BindingResult bindingResult){

        try {

            if (bindingResult.hasErrors()) {
                log.error("Erro na Requisição: {}", bindingResult.toString());
                return badRequest().body(BuildErrorResponse(bindingResult));
            } else {
                Product s = service.create(request);
                log.info("Product adicionado com sucesso:  [{}]", s);

                ProductResponseBody responseBody = new ProductResponseBody();
                responseBody.setDescription("success");
                responseBody.getProductList().add(s);

                return ok().body(responseBody);
            }
        }catch (Exception e){
            log.error("Falha no cadastro: {}", e.getMessage(), e);
            ProductResponseBody responseBody = new ProductResponseBody();
            responseBody.setDescription("Fail: Erro ao requisitar cadastro.");
            return ResponseEntity.status(500).body(responseBody);
        }
    }
    @RequestMapping(path = "/products", method = RequestMethod.PUT)
    public HttpEntity<ProductResponseBody> update(@Valid @RequestBody ProductRequestBody request, BindingResult
            bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao: {}", bindingResult.toString());
                return badRequest().body(BuildErrorResponse(bindingResult));
            } else if (request.getEntity().getIdProduct() == null) {
                ProductResponseBody responseBody = new ProductResponseBody();
                responseBody.setDescription("Entity ID must to be set!");
                return badRequest().body(responseBody);
            } else {
                Optional<Product> optional = service.update(request);

                if (optional.isPresent()) {
                    Product updatedProduct = optional.get();

                    ProductResponseBody responseBody = new ProductResponseBody();
                    responseBody.getProductList().add(updatedProduct);

                    log.info("Product atualizado com sucesso:  [{}]", updatedProduct);

                    return ok().body(responseBody);
                } else {
                    log.error("Product nao encontrada: idRole[{}]", request.getEntity().getIdProduct());
                    return notFound().build();
                }
            }
        } catch (Exception e) {
            log.error("Falha na atualizacao: {}", e.getMessage(), e);
            ProductResponseBody responseBody = new ProductResponseBody();
            responseBody.setDescription("Fail: Erro ao requisitar atualização.");
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/products/{id}", method = RequestMethod.GET)
    public HttpEntity<ProductResponseBody> findById(@PathVariable String id) {

        try {
            Optional<Product> optional = service.find(Long.valueOf(id));

            if (optional.isPresent()) {

                Product foundProduct = optional.get();
                ProductResponseBody response = new ProductResponseBody();
                response.setDescription("Role succesfully retrieved");
                response.getProductList().add(foundProduct);

                log.info("Busca de Product com exito: [{}]", foundProduct);

                return ok().body(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", id);
                return notFound().build();
            }
        } catch (Exception e) {
            log.error("Falha na busca por ID: {}", e.getMessage(), e);
            ProductResponseBody responseBody = new ProductResponseBody();
            responseBody.setDescription("Fail: Erro ao requisitar pesquisa.");
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public HttpEntity<ProductResponseBody> findAll() {

        try {
            List<Product> entitylist = service.findAll();

            ProductResponseBody responseBody = new ProductResponseBody();
            responseBody.setProductList(entitylist);
            responseBody.setDescription("Success");

            log.info("{} Product successfully retrieved.", entitylist.size());

            return ok().body(responseBody);
        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            ProductResponseBody responseBody = new ProductResponseBody();
            responseBody.setDescription("Fail: Erro ao requisitar pesquisa de todos os registros");
            return ResponseEntity.status(500).body(responseBody);
        }
    }

    private ProductResponseBody BuildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());
        ProductResponseBody responseBody = new ProductResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;

    }
}
