package com.cosmeticos.subscription.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 17/07/2017.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(ProductRequestBody requestBody){

        Product product = new Product();
        product.setNameProduct(requestBody.getEntity().getNameProduct());
        product.setDescriptionProduct(requestBody.getEntity().getDescriptionProduct());
        product.setStatusProduct(requestBody.getEntity().getStatusProduct());
        return productRepository.save(product);

    }

    public Optional<Product> update(ProductRequestBody requestBody){

        Product productFromRequest = requestBody.getEntity();

        Long requestedIdProduct = productFromRequest.getIdProduct();

        Optional<Product> optional = Optional.ofNullable(productRepository.findOne(requestedIdProduct));

        if(optional.isPresent()){
            Product persistentProduct = optional.get();

            persistentProduct.setNameProduct(productFromRequest.getNameProduct());
            persistentProduct.setDescriptionProduct(productFromRequest.getDescriptionProduct());
            persistentProduct.setStatusProduct(productFromRequest.getStatusProduct());

            productRepository.save(persistentProduct);

        }

        return optional;

    }

    public Optional<Product> find(Long id){
        return Optional.ofNullable(productRepository.findOne(id));
    }

    public List<Product> findAll(){

        Iterable<Product> result = productRepository.findAll();

        return StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());

    }

    public void delete() {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

}
