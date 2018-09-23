package com.cosmeticos.service;

import com.cosmeticos.model.BankAccount;
import com.cosmeticos.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 */
@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository repository;

    public Optional<BankAccount> findById(Long id)
    {
        return (repository.findById(id));
    }

    public void update(BankAccount bankAccount) {
        BankAccount persistentBankAccount = findById(bankAccount.getId()).get();

        persistentBankAccount.setFinancialInstitute(bankAccount.getFinancialInstitute());
        persistentBankAccount.setAgency(bankAccount.getAgency());
        persistentBankAccount.setAccountNumber(bankAccount.getAccountNumber());
        persistentBankAccount.setType(bankAccount.getType());
        persistentBankAccount.setOwnerName(bankAccount.getOwnerName());
        persistentBankAccount.setOwnerCPF(bankAccount.getOwnerCPF());

        repository.save(persistentBankAccount);
    }
}
