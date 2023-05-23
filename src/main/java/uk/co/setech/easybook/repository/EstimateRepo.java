package uk.co.setech.easybook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.model.Estimate;
import uk.co.setech.easybook.model.User;

import java.util.List;
import java.util.Optional;

public interface EstimateRepo extends JpaRepository<Estimate, Long> {
    Optional<Estimate> findByIdAndUser(Long id, User user);

    List<Estimate> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);
}
