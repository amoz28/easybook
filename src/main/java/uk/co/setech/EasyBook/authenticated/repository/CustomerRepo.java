package uk.co.setech.EasyBook.authenticated.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.authenticated.model.Customer;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    void deleteByEmail(String email);
}
