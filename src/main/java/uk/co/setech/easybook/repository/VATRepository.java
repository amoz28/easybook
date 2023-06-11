package uk.co.setech.easybook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.setech.easybook.model.VAT;

@Repository
public interface VATRepository extends JpaRepository<VAT, Long> {
}
