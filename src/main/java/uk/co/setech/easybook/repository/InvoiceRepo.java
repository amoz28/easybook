package uk.co.setech.easybook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uk.co.setech.easybook.dto.InvoicePaymentInfo;
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
    List<Invoice> findAllInvoiceByUserIdAndCustomerIdAndType(long userId, long customerId, InvoiceType type );
    List<Invoice> findAllInvoiceByUserIdAndCustomerId(long userId, long customerId);

    @Modifying
    @Transactional
    @Query("UPDATE Invoice SET isInvoicePaid = true WHERE id = :invoiceId")
    void markInvoiceAsPaid(Long invoiceId);


    @Query(value = "SELECT(" +
            "SELECT COALESCE(SUM(total),0) FROM invoice WHERE is_invoice_paid = false AND duedate < current_timestamp and user_id=:userId and type =:type) AS overdueInvoiceTotal, " +
            "(SELECT COALESCE(SUM(total), 0) FROM invoice WHERE is_invoice_paid = true and user_id=:userId and type =:type) AS paidInvoiceTotal", nativeQuery = true)
    InvoicePaymentInfo getOverdueAndPaidInvoice(Long userId, String type);
}
