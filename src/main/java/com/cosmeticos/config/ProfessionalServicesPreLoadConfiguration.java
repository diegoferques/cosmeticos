package com.cosmeticos.config;

import com.cosmeticos.model.Professional;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.model.Service;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.repository.ProfessionalServicesRepository;
import com.cosmeticos.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Vinicius on 20/06/2017.
 */
@Configuration
@Profile("default")
public class ProfessionalServicesPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    /*
   @Autowired
    private ProfessionalServicesRepository repository;
*/

    @PostConstruct
    public void insertInitialH2Data(){

        // Criamos o Usuario que nao existe no banco.
        User user = new User();
        user.setUsername("username");

        // Criamos um profissional
        Professional p = new Professional();
        p.setIdLogin(user);// Associamos o User acima ao Profissional.
        p.setNameProfessional("JOSICREIDE");

        // Inserimos JOSICREIDE no banco com o username "username" rsrs..
        // Repare que "username" nao existe no banco e nao faço o insert dele.
        // Quando faco o insert do profissional aqui, o hibernate detecta que o User "username" nao existe e também o cria.

        professionalRepository.save(p);


        Service s = new Service();
        s.setCategory("MANICURE");
        serviceRepository.save(s);

        // Segundo Preload
        User user2 = new User();
        user2.setUsername("username2");

        Professional p2 = new Professional();
        p2.setIdLogin(user2);
        p2.setNameProfessional("ROSIVALDA");
        professionalRepository.save(p2);

        Service s2 = new Service();
        s2.setCategory("PEDICURE");
        serviceRepository.save(s2);

    }

}
