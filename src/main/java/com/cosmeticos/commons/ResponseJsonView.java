package com.cosmeticos.commons;

import com.cosmeticos.controller.ProfessionalCategoryController;
import com.cosmeticos.model.ProfessionalCategory;

/**
 * Visa determinar que campos serao retornados de uma classe no json de acordo com um contexto.
 * Iniciaremos com uma inner interface por endpoint method.
 * Inspiracao: https://github.com/sdeleuze/spring-jackson-demo
 * Created by Lulu on 05/07/2017.
 */
public class ResponseJsonView {

	/**
     * Especifica que campos da entidade o endpoint mapeado por
     * {@link ProfessionalCategoryController#findAll(ProfessionalCategory)}
     * serializara no json.
     */
    public interface ProfessionalCategoryFindAll {}
    public interface WalletsFindAll {}
    public interface CreditCardFindAll{}
    public interface OrderControllerCreate {}
    public interface OrderControllerUpdate {}
    public interface OrderControllerFindBy {}
    public interface ProfessionalFindAll {}
    public interface ProfessionalUpdate {}
    public interface ProfessionalCreate {}
	public interface CategoryGetAll {	}
	public interface ScheduleByProfessionalInRunningOrders {}

    public interface CustomerControllerUpdate {
    }

    public interface CustomerControllerGet {
    }
}
