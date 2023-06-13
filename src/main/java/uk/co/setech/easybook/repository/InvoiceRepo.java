package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.model.Invoice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByIdAndUserId(Long id, long userId);

    List<Invoice> findByUserIdAndTypeOrderByIdDesc(long userId, InvoiceType type);

    List<Invoice> findByUserIdOrderByIdDesc(long userId);

    @Transactional
    void deleteByIdAndUserId(Long id, long userId);

    List<Invoice> findByIsInvoicePaidIsFalseAndLastReminderDateBefore(LocalDate date);

    Page<Invoice> findAllInvoiceByUserIdOrderByIdDesc(long userId, Pageable pageable);

    Page<Invoice> findAllInvoiceByUserIdAndTypeInOrderByIdDesc(long userId, Pageable pageable, InvoiceType... type);

    List<Invoice> findAllInvoiceByUserIdAndCustomerIdOrderByIdDesc(long userId, long customerId);
}
