package uk.co.setech.EasyBook.authenticated.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.EasyBook.authenticated.model.Invoice;

import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

//    Optional<Invoice> findById(String email);

//    void deleteById(String email);
}
