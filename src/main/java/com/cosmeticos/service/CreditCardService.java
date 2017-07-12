package com.cosmeticos.service;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Order;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Vinicius on 07/07/2017.
 */
@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository repository;

    @Autowired
    private UserRepository userRepository;

    public List<CreditCard> findAll() {

        User u = userRepository.findOne(1L);

        //Iterable<CreditCard> result = repository.findAll();
        List<CreditCard> savedOrders = repository.findByUserEmail(u);

        return StreamSupport.stream(savedOrders.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Usa a api Example do spring-data.
     * @param creditCardProbe
     * @return
     */

    public List<CreditCard> findAllBy(CreditCard creditCardProbe) {
        return this.repository.findAll(Example.of(creditCardProbe));
    }// repara que a query vai mudar.
}
