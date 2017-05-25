package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    //TODO - Alterar o atributo "idCustomer" para somente "id"
    public Customer findByIdCustomer(Long id);
    public Customer findByIdLogin(User idLogin);
    public Customer findByCpf(String cpf);

    //TODO - Alterar o atributo "nameCustomer" para somente "name"
    public List<Customer> findByNameCustomer(String nameCustomer);
    public List<Customer> findByGenre(char genre);
    public List<Customer> findByBirthDate(String birthDate);
    public List<Customer> findByCellPhone(String cellPhone);
    public List<Customer> findByStatus(short status);

    public List<Customer> findAllByOrderByIdCustomer();
    public List<Customer> findAllByOrderByIdCustomerDesc();
    public List<Customer> findAllByOrderByNameCustomer();
    public List<Customer> findAllByOrderByNameCustomerDesc();
    public List<Customer> findAllByOrderByBirthDate();
    public List<Customer> findAllByOrderByBirthDateDesc();
    public List<Customer> findAllByOrderByDateRegister();
    public List<Customer> findAllByOrderByDateRegisterDesc();
}
