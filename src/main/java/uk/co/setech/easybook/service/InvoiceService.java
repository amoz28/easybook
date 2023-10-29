package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.dto.InvoicePaymentInfo;
import uk.co.setech.easybook.enums.InvoiceType;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto customerDto);

    InvoiceDto updateInvoice(InvoiceDto customerDto);

    List<InvoiceDto> getAllInvoice(String type);

    List<InvoiceDto> getAllInvoiceByCustomerIdAndType(Long customerId, String type);

    List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize, String... type);

    List<InvoiceDto> getInvoiceDtos(long userId, InvoiceType invoiceType);

    InvoiceDto getInvoiceById(long invoiceId);

    GeneralResponse deleteInvoiceById(long invoiceId);

    void sendInvoiceReminder();

    GeneralResponse addPayment(Long invoiceId);

    GeneralResponse markAsSent(Long invoiceId);

    GeneralResponse resendInvoice(Long invoiceId);

    InvoicePaymentInfo getOverdueAndPaidInvoice(Long id, InvoiceType invoice);
}
