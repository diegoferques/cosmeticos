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
    public Customer findByCustomerIdCustomer(Long id);
    public Customer findByCustomerIdLogin(User idLogin);
    public Customer findByCustomerCpf(String cpf);

    //TODO - Alterar o atributo "nameCustomer" para somente "name"
    public List<Customer> findByCustomerNameCustomer(String nameCustomer);
    public List<Customer> findByCustomerGenre(char genre);
    public List<Customer> findByCustomerBirthDate(String birthDate);
    public List<Customer> findByCustomerCellPhone(String cellPhone);
    public List<Customer> findByCustomerStatus(short status);

    public List<Customer> findAllByOrderById();
    public List<Customer> findAllByOrderByIdDesc();
    public List<Customer> findAllByOrderByNameCustomer();
    public List<Customer> findAllByOrderByNameCustomerDesc();
    public List<Customer> findAllByOrderByBirthDate();
    public List<Customer> findAllByOrderByBirthDateDesc();
    public List<Customer> findAllByOrderByDateRegister();
    public List<Customer> findAllByOrderByDateRegisterDesc();
}
