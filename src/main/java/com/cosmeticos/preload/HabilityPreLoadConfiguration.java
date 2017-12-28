package com.cosmeticos.preload;

import com.cosmeticos.model.Hability;
import com.cosmeticos.repository.HabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Classe que so vai executar em dev, pois o profile de producao sera PRODUCTION.
 * Created by Lulu on 26/05/2017.
 */
@Configuration
@Profile("default")
public class HabilityPreLoadConfiguration {

    @Autowired
    private HabilityRepository repository;

    @PostConstruct
    public void insertInitialH2Data()
    {
        Hability s1 = new Hability("Escova Progressiva");
        Hability s2 = new Hability("Escova Simples");
        Hability s3 = new Hability("Prancha");
        Hability s4 = new Hability("Corte");
        Hability s5 = new Hability("Hidratação");
        Hability s6 = new Hability("Penteados");
        Hability s7 = new Hability("Dia da Noiva");
        Hability s8 = new Hability("Pintura");
        Hability s9 = new Hability("Luzes");
        Hability s10 = new Hability("Platinado");
        Hability s11 = new Hability("Restauração");
        Hability s12 = new Hability("Manicure");
        Hability s13 = new Hability("Relaxamento");

        repository.save( s1 );
        repository.save( s2 );
        repository.save( s3 );
        repository.save( s4 );
        repository.save( s5 );
        repository.save( s6 );
        repository.save( s7 );
        repository.save( s8 );
        repository.save( s9 );
        repository.save( s10);
        repository.save( s11);
        repository.save( s12);
        repository.save( s13);

    }
}
