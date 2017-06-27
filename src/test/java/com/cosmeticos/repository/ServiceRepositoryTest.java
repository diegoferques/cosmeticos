package com.cosmeticos.repository;

import com.cosmeticos.model.Service;
import org.junit.Assert;
import org.junit.Before;
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
public class ServiceRepositoryTest {

        @Autowired
        private ServiceRepository repository;
    private Long id;

    /**
         * Inicializa o H2 com dados iniciais.
         */
        @Before
        public void setupTests()
        {
            Service s1 = new Service();
            s1.setCategory("HAIR 123456");

            repository.save(s1);
id = s1.getIdService();
        }

        @Test
        public void testFindServiceCabelo() {
            Service service = repository.findOne(id);
            Assert.assertNotNull(service);

            // Confere se o Service que retornou confere com o primeiro Service inserido.
            Assert.assertEquals("HAIR 123456", service.getCategory());
        }

}
