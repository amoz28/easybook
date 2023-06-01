package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.email.EmailService;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.service.CustomerService;
import uk.co.setech.easybook.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;
import static uk.co.setech.easybook.utils.Utils.getNullPropertyNames;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepo invoiceRepo;
    private final EmailService emailService;
    private final CustomerService customerService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        Integer customerId = invoiceDto.getCustomerId();
        var customer = customerService.getCustomerById(customerId);
        invoiceDto.setCustomerId(customer.getId());
        var invoice = dtoToInvoice(invoiceDto, Invoice.builder().build());
        long userId = getCurrentUserDetails().getId();
        invoice.setUserId(userId);
        invoice = invoiceRepo.save(invoice);
        return invoiceToDto(invoice);
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        long userId = getCurrentUserDetails().getId();
        var invoice = invoiceRepo.findByIdAndUserId(invoiceDto.getId(), userId)
                .orElseThrow(() -> new IllegalStateException("Invalid invoice number"));
        dtoToInvoice(invoiceDto, invoice);
        var savedInvoice = invoiceRepo.save(invoice);
        return invoiceToDto(savedInvoice);
    }

    @Override
    public List<InvoiceDto> getAllInvoice(String type) {
        long userId = getCurrentUserDetails().getId();
        InvoiceType invoiceType = type == null ? null : InvoiceType.valueOf(type);
        return getInvoiceDtos(userId, invoiceType);
    }

    @Override
    public List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize, String type) {
        long userId = getCurrentUserDetails().getId();
        Sort descendingSort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageable = PageRequest.of(pageNo, pageSize, descendingSort);
        Page<Invoice> invoices = type == null
                ? invoiceRepo.findAllInvoiceByUserId(userId, pageable)
                : invoiceRepo.findAllInvoiceByUserIdAndType(userId, pageable, InvoiceType.valueOf(type));
        return invoices
                .map(this::invoiceToDto)
                .getContent();
    }

    @Override
    public List<InvoiceDto> getInvoiceDtos(long userId, InvoiceType invoiceType) {
        List<Invoice> allUserInvoices = invoiceType == null
                ? invoiceRepo.findByUserId(userId)
                :invoiceRepo.findByUserIdAndType(userId, invoiceType);
        return allUserInvoices
                .stream()
                .map(this::invoiceToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto getInvoiceById(long invoiceId) {
        long userId = getCurrentUserDetails().getId();
        return invoiceRepo.findByIdAndUserId(invoiceId, userId)
                .map(this::invoiceToDto
                )
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice Id not found"));
    }

    @Override
    public GeneralResponse deleteInvoiceById(long invoiceId) {
        long userId = getCurrentUserDetails().getId();
        invoiceRepo.deleteByIdAndUserId(invoiceId, userId);
        return GeneralResponse.builder()
                .message("Invoice deleted")
                .build();
    }


    private InvoiceDto invoiceToDto(Invoice invoice) {
        var invoiceDto = InvoiceDto.builder().build();
        BeanUtils.copyProperties(invoice, invoiceDto);
        return invoiceDto;
    }

    private Invoice dtoToInvoice(InvoiceDto invoiceDto, Invoice invoice) {
        BeanUtils.copyProperties(invoiceDto, invoice, getNullPropertyNames(invoiceDto));
        return invoice;
    }

    @Override
    public void sendInvoiceReminder() {
        var message = "Your invoice attached to this mail is still outstanding please pay up";
        invoiceRepo
                .findByIsInvoicePaidIsFalseAndLastReminderDateBefore(LocalDate.now())
                .forEach(invoice -> {
                    CustomerDto customer = customerService.getCustomerById(invoice.getCustomerId());
                    emailService.send(customer.getFirstname(), customer.getEmail(), message, "INVOICE REMINDER");
                });
    }

    @Override
    public GeneralResponse addPayment(Long invoiceId) {
        invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new IllegalStateException("Invoice not found"));
        return GeneralResponse.builder()
                .message("Payment was successfully updated")
                .build();
    }
}
