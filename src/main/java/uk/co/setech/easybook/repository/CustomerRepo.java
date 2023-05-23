package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.model.User;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {


    Optional<Customer> findByEmailAndUser(String email, User user);

    Page<Customer> findAllByUser(User user, Pageable pageable);

    List<Customer> findAllByUser(User user);

    void deleteByEmail(String email);
}
