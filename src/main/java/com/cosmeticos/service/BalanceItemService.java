package com.cosmeticos.service;

import com.cosmeticos.model.BalanceItem;
import com.cosmeticos.model.Order;
import com.cosmeticos.repository.BalanceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static java.time.LocalDateTime.now;

/**
 *
 */
@Service
public class BalanceItemService {

    @Autowired
    private BalanceItemRepository repository;

    public List<BalanceItem> listBy(BalanceItem balanceItem) {
        return repository.findAll(Example.of(balanceItem));
    }

    /**
     * Criamos assincronamente pois eventuais falhas neste procedimento nao podem impactar na execucao da transacao.
     * @param balanceItem
     * @return
     */
    @Async
    public BalanceItem create(BalanceItem balanceItem) {
        return repository.save(balanceItem);
    }

    public static BalanceItem creditFromOrder(Order order) {
        return fromOrder(order, BalanceItem.Type.CREDIT);
    }

    public static BalanceItem debitFromOrder(Order order) {
        return fromOrder(order, BalanceItem.Type.WITHDRALL);
    }

    private static BalanceItem fromOrder(Order order, BalanceItem.Type type) {
        BalanceItem item = new BalanceItem();
        item.setDescription(String.format("%s %s", order.getClass().getSimpleName(), String.valueOf(order.getStatus())));
        item.setType(type);
        item.setOrderId(order.getIdOrder());
        item.setDate(Timestamp.valueOf(now()));
        item.setEmail(order
                .getProfessionalCategory()
                .getProfessional()
                .getUser()
                .getEmail()
        );
        item.setValue(order
                .getPaymentCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRule()
                .getPrice()
        );

        return item;
    }

    public List<BalanceItem> findByProfessional(String email) {
        return repository.findByEmail(email);
    }

    public Long sum(List<BalanceItem> balanceItems) {

        Long total = balanceItems.stream().mapToLong(i -> i.getValue()).sum();

        return total;
    }

    public List<BalanceItem> listAll() {
        return repository.findAll();
    }

    /**
     * O resgate eh sempre total. O que o profissional tiver na conta sera transferido na sua totalidade.
     * @param email
     * @return The full balance withdrawaled
     */
    public Long withdrawal(String email) {
        List<BalanceItem> balanceItens = findByProfessional(email);

        Long balance = balanceItens.stream().mapToLong(i -> i.getValue()).sum();

        BalanceItem item = new BalanceItem();
        item.setDate(Timestamp.valueOf(now()));
        item.setType(BalanceItem.Type.WITHDRALL);
        item.setEmail(email);
        item.setDescription("Resgate de saldo");
        item.setValue(balance < 0 ? balance : -balance);

        create(item);

        return balance;
    }
}
