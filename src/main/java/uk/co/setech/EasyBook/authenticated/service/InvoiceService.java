package uk.co.setech.EasyBook.authenticated.service;

import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto customerDto);

    InvoiceDto updateInvoice(InvoiceDto customerDto);

    List<InvoiceDto> getAllInvoice();

    InvoiceDto getInvoiceById(String invoiceId);

    GeneralResponse deleteInvoiceById(String email);

    void sendInvoiceReminder();
}
