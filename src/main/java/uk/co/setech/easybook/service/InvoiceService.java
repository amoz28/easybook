package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto customerDto);

    InvoiceDto updateInvoice(InvoiceDto customerDto);

    List<InvoiceDto> getAllInvoice();

    List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize);

    List<InvoiceDto> getInvoiceDtos(String email);
    InvoiceDto getInvoiceById(String invoiceId);

    GeneralResponse deleteInvoiceById(String email);

    void sendInvoiceReminder();

    GeneralResponse addPayment(Long invoiceId);
}
