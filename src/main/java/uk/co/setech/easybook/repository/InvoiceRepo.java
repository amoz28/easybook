package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.model.Invoice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByIdAndUserId(Long id, long userId);

    List<Invoice> findByUserIdAndType(long userId, InvoiceType type);

    List<Invoice> findByUserId(long userId);

    void deleteByIdAndUserId(Long id, long userId);

    List<Invoice> findByIsInvoicePaidIsFalseAndLastReminderDateBefore(LocalDate date);

    Page<Invoice> findAllInvoiceByUserId(long userId, Pageable pageable);

    Page<Invoice> findAllInvoiceByUserIdAndType(long userId, Pageable pageable, InvoiceType type);

//    void updateIsInvoicePaidById(Long invoiceId, boolean isPaid);
}
