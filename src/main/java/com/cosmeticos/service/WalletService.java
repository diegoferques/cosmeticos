package com.cosmeticos.service;

import com.cosmeticos.model.Wallet;
import com.cosmeticos.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Service
public class WalletService {

    @Autowired
    private WalletRepository repository;

    /**
     * Usa a api Example do spring-data.
     * @param walletProbe
     * @return
     */
    public List<Wallet> findAllBy(Wallet walletProbe) {

        return this.repository.findAll(Example.of(walletProbe));
    }
}
