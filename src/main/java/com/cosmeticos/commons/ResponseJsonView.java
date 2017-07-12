package com.cosmeticos.commons;

import com.cosmeticos.model.ProfessionalServices;

/**
 * Visa determinar que campos serao retornados de uma classe no json de acordo com um contexto.
 * Iniciaremos com uma inner interface por endpoint method.
 * Inspiracao: https://github.com/sdeleuze/spring-jackson-demo
 * Created by Lulu on 05/07/2017.
 */
public class ResponseJsonView {

    /**
     * Especifica que campos da entidade o endpoint mapeado por
     * {@link com.cosmeticos.controller.ProfessionalServicesController#findAll(ProfessionalServices)}
     * serializara no json.
     */
    public interface ProfessionalServicesFindAll {}
    public interface WalletsFindAll {}
    public interface CreditCardFindAll{}

    // Comecou a ficar complicado.
    public interface OrderControllerCreate extends ProfessionalServicesFindAll {}
}
