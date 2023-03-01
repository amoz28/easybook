package uk.co.setech.EasyBook.authenticated.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.InvoiceDto;
import uk.co.setech.EasyBook.authenticated.model.Invoice;
import uk.co.setech.EasyBook.authenticated.repository.CustomerRepo;
import uk.co.setech.EasyBook.authenticated.repository.InvoiceRepo;
import uk.co.setech.EasyBook.utils.ExcludeNullValues;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepo invoiceRepo;

    private final CustomerRepo customerRepo;
    private final ExcludeNullValues excludeNullValues;

    /**
     * @param invoiceDto
     * @return
     */
    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        var customer = customerRepo.findByEmail(invoiceDto.getCustomerEmail())
                .orElseThrow(()-> new IllegalArgumentException("Customer does not exist"));
        invoiceDto.setCustomer(customer);

        var invoice = dtoToInvoice(invoiceDto, Invoice.builder().build());

        invoice = invoiceRepo.save(invoice);

        return invoiceToDto(invoice, invoiceDto);
    }


    /**
     * @param invoiceDto
     * @return
     */
    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        var invoice = invoiceRepo.findById(invoiceDto.getId())
                .orElseThrow(()-> new IllegalStateException("Invalid invoice number"));

        invoice = dtoToInvoice(invoiceDto, invoice);

        var savedInvoice = invoiceRepo.save(invoice);

        return invoiceToDto(savedInvoice, invoiceDto);
    }

    /**
     * @return
     */
    @Override
    public List<InvoiceDto> getAllInvoice() {

        return invoiceRepo.findAll().stream()
            .map(invoice -> invoiceToDto(invoice, InvoiceDto.builder().build()))
            .collect(Collectors.toList());
    }

    /**
     * @param invoiceId
     * @return
     */
    @Override
    public InvoiceDto getInvoiceById(String invoiceId) {

        return invoiceRepo.findById(Long.valueOf(invoiceId))
                .map(invoice ->
                    invoiceToDto(invoice, InvoiceDto.builder().build())
                )
                .orElseThrow(()-> new IllegalArgumentException("Invoice Id not found"));
    }

    /**
     * @param invoiceId
     * @return
     */
    @Override
    public GeneralResponse deleteInvoiceById(String invoiceId) {
        invoiceRepo.deleteById(Long.valueOf(invoiceId));

        return GeneralResponse.builder()
                .message("Invoice deleted")
                .build();
    }


    private InvoiceDto invoiceToDto(Invoice invoice, InvoiceDto invoiceDto) {
        BeanUtils.copyProperties(invoice, invoiceDto);

        return invoiceDto;
    }

    private Invoice dtoToInvoice(InvoiceDto invoiceDto, Invoice invoice) {
        BeanUtils.copyProperties(invoiceDto, invoice, excludeNullValues.getNullPropertyNames(invoiceDto));

        return invoice;
    }
}
