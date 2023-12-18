package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.*;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.model.PaymentRequest;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto customerDto);

    InvoiceDto updateInvoice(InvoiceDto customerDto);

    List<InvoiceDto> getAllInvoice(String type);

    List<InvoiceDto> getAllInvoiceByCustomerIdAndType(Long customerId, String type);

    List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize, String... type);

    List<InvoiceDto> getOverdueInvoicesWithSize(int pageNo, int pageSize, String... type);

    public AuthenticationResponse getUserProfile();
    List<InvoiceDto> getInvoiceDtos(long userId, InvoiceType invoiceType);

    InvoiceDto getInvoiceById(long invoiceId);

    GeneralResponse deleteInvoiceById(long invoiceId);

    void sendInvoiceReminder();

    GeneralResponse addPayment(PaymentRequest paymentRequest);

    GeneralResponse markAsSent(Long invoiceId);

    GeneralResponse sendInvoice(Long invoiceId);

    InvoicePaymentInfo getOverdueAndPaidInvoice(Long id, InvoiceType invoice);
}
