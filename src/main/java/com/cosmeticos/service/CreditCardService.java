package com.cosmeticos.service;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import com.cosmeticos.repository.CreditCardRepository;
import com.cosmeticos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        List<CreditCard> savedOrders = repository.findByUserEmail(u.getEmail());

        return StreamSupport.stream(savedOrders.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Usa a api Example do spring-data.
     *
     * @param creditCardProbe
     * @return
     */

    public List<CreditCard> findAllBy(CreditCard creditCardProbe) {
        return this.repository.findAll(Example.of(creditCardProbe));
    }// repara que a query vai mudar.

    public CreditCard create(CreditCard creditCard) {

        Optional<User> persistentUserOptional = Optional.ofNullable(userRepository.findOne(creditCard.getUser().getIdLogin()));

        if (persistentUserOptional.isPresent()) {
            User persistentUser = persistentUserOptional.get();

            Optional<CreditCard> activeCard = persistentUser.getCreditCardCollection()
                    .stream()
                    .filter(c -> CreditCard.Status.ACTIVE.equals(c.getStatus()))
                    .findFirst();

            if (activeCard.isPresent()) {
                throw new IllegalStateException("Usuario ja possui um cartao cadastrado.");
            } else {
                return this.repository.save(creditCard);
            }
        } else {
            throw new IllegalStateException("Usuario nao informado na requisicao");
        }
    }

    public CreditCard update(CreditCard creditCardFromRequest) {

        Optional<CreditCard> persistentCreditCardOptional = Optional.ofNullable(repository.findOne(creditCardFromRequest.getIdCreditCard()));

        if (persistentCreditCardOptional.isPresent()) {

            CreditCard persistentCreditCard = persistentCreditCardOptional.get();

            if(creditCardFromRequest.getStatus() == CreditCard.Status.INACTIVE) {
                persistentCreditCard.setStatus(CreditCard.Status.INACTIVE);

            } else {

                if(creditCardFromRequest.getStatus() != persistentCreditCard.getStatus()) {
                    persistentCreditCard.setStatus(creditCardFromRequest.getStatus());
                }

                if(creditCardFromRequest.getExpirationDate() != persistentCreditCard.getExpirationDate()) {
                    persistentCreditCard.setExpirationDate(creditCardFromRequest.getExpirationDate());
                }

                if(creditCardFromRequest.getOwnerName() != persistentCreditCard.getOwnerName()) {
                    persistentCreditCard.setOwnerName(creditCardFromRequest.getOwnerName());
                }

                if(creditCardFromRequest.getSecurityCode() != persistentCreditCard.getSecurityCode()) {
                    persistentCreditCard.setSecurityCode(creditCardFromRequest.getSecurityCode());
                }

                if(creditCardFromRequest.getVendor() != persistentCreditCard.getVendor()) {
                    persistentCreditCard.setVendor(creditCardFromRequest.getVendor());
                }

                if(creditCardFromRequest.getNumber() != persistentCreditCard.getNumber()) {
                    persistentCreditCard.setNumber(creditCardFromRequest.getNumber());
                }

                if(creditCardFromRequest.getOneClick() != persistentCreditCard.getOneClick()) {
                    persistentCreditCard.setOneClick(creditCardFromRequest.getOneClick());
                }
            }

            return this.repository.save(persistentCreditCard);

        } else {
            throw new IllegalStateException("O Cartão de crédito não foi localizado");
        }
    }

}
