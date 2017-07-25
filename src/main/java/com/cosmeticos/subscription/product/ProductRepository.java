package com.cosmeticos.subscription.product;

import com.cosmeticos.subscription.product.Product;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Vinicius on 17/07/2017.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
}
