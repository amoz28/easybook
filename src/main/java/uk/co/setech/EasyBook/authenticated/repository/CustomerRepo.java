package uk.co.setech.EasyBook.authenticated.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.authenticated.model.Customer;
import uk.co.setech.EasyBook.user.User;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {


    Optional<Customer> findByEmailAndUser(String email, User user);

    List<Customer> findAllByUser(User user);
    void deleteByEmail(String email);
}
