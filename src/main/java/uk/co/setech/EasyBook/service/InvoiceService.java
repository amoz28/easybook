package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto customerDto);

    InvoiceDto updateInvoice(InvoiceDto customerDto);

    List<InvoiceDto> getAllInvoice();

    InvoiceDto getInvoiceById(String invoiceId);

    GeneralResponse deleteInvoiceById(String email);

}
