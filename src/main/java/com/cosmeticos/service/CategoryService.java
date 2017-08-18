package com.cosmeticos.service;

import com.cosmeticos.commons.CategoryRequestBody;
import com.cosmeticos.model.Category;
import com.cosmeticos.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 31/05/2017.
 */
@org.springframework.stereotype.Service
public class CategoryService {


   @Autowired
   private CategoryRepository repository;

    public Category create(CategoryRequestBody request) {
        Category s = new Category();
        s.setName(request.getEntity().getName());

        return repository.save(s);
    }

    public Optional<Category> update(CategoryRequestBody request) {
        Category categoryFromRequest = request.getEntity();

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdCategory = categoryFromRequest.getIdCategory();

        Optional<Category> optional = Optional.ofNullable(repository.findOne(requestedIdCategory));

        if (optional.isPresent()) {
            Category persistentCategory = optional.get();

            persistentCategory.setName(categoryFromRequest.getName());
            persistentCategory.setProfessionalServicesCollection(categoryFromRequest.getProfessionalServicesCollection());

            repository.save(persistentCategory);
        }

        return optional;
    }

    public Optional<Category> find(Long id){
        return Optional.ofNullable(repository.findOne(id));
    }

    public void deletar(){
        throw new UnsupportedOperationException("Excluir de acordo com o Status. ");
    }

    public List<Category> findAll() {

        Iterable<Category> result = repository.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
    }
}
