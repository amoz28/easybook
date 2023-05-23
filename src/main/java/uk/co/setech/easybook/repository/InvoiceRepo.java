package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByIdAndUser(Long id, User user);

    List<Invoice> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);

    List<Invoice> findByUserAndIsInvoicePaidIsFalseAndLastReminderDateBefore(User user, LocalDate date);

    Page<Invoice> findAllInvoiceByUser(User user, Pageable pageable);

//    void updateIsInvoicePaidById(Long invoiceId, boolean isPaid);
}
