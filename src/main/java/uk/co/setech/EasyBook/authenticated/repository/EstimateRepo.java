package uk.co.setech.EasyBook.authenticated.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.authenticated.model.Estimate;

public interface EstimateRepo extends JpaRepository<Estimate, Long> {

}
