package com.cosmeticos.repository;

import com.cosmeticos.model.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Vinicius on 31/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryRepositoryTest {

        @Autowired
        private CategoryRepository repository;
    private Long id;

    /**
         * Inicializa o H2 com dados iniciais.
         */
        @Before
        public void setupTests()
        {

            Category category = repository.findByName("HAIR 123456");

            if(category == null) {
                category = new Category();
                category.setName("HAIR 123456");
                repository.save(category);
            }

            id = category.getIdCategory();
        }

        @Test
        public void testFindServiceCabelo() {
            Category service = repository.findOne(id);
            Assert.assertNotNull(service);

            // Confere se o Service que retornou confere com o primeiro Service inserido.
            Assert.assertEquals("HAIR 123456", service.getName());
        }

        @Ignore
        @Test
        public void testOwnerCategory(){

            Category owner = new Category();
            owner.setName("testOwnerCategory");

            Category child = new Category();
            child.setName("testOwnerCategory-child");

            child.setOwnerCategory(owner);
            owner.getChildrenCategories().add(child);

            repository.save(owner);

            Category c = repository.findOne(child.getIdCategory());
            Assert.assertNotNull(c);
            Assert.assertNotNull(c.getOwnerCategory());
            Assert.assertEquals(c.getOwnerCategory().getIdCategory(), owner.getIdCategory());
        }

}
