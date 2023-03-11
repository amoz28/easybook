package uk.co.setech.EasyBook.authenticated.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.authenticated.model.Estimate;
import uk.co.setech.EasyBook.authenticated.model.Invoice;
import uk.co.setech.EasyBook.user.User;

import java.util.List;
import java.util.Optional;

public interface EstimateRepo extends JpaRepository<Estimate, Long> {
    Optional<Estimate> findByIdAndUser(Long id, User user);

    List<Estimate> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);
}
