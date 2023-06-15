package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByIdAndUserId(long id, long userId);

    Optional<Customer> findByEmailIgnoreCaseAndUserId(String email, long userId);

    Page<Customer> findAllByUserId(long userId, Pageable pageable);

    List<Customer> findAllByUserId(long userId);
}
