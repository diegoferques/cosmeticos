package com.cosmeticos.repository;

import com.cosmeticos.model.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Callable;

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
            Category s1 = new Category();
            s1.setName("HAIR 123456");

            repository.save(s1);
            id = s1.getIdCategory();
        }

        @Test
        public void testFindServiceCabelo() {
            Category service = repository.findOne(id);
            Assert.assertNotNull(service);

            // Confere se o Service que retornou confere com o primeiro Service inserido.
            Assert.assertEquals("HAIR 123456", service.getName());
        }

        @Test
        public void testOwnerCategory(){

            Category ownerCategory = new Category();
            ownerCategory.setName("CABELEIREIRA");

            Category owneredCategory = new Category();
            owneredCategory.setName("ESCOVISTA");

            owneredCategory.setOwnerCategory(ownerCategory);

            repository.save(owneredCategory);

            Category c = repository.findOne(owneredCategory.getIdCategory());
            Assert.assertNotNull(c);
            Assert.assertNotNull(c.getOwnerCategory());



        }

}
