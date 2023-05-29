package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.email.EmailService;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.repository.CustomerRepo;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.InvoiceService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.setech.easybook.utils.Utils.*;
import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final InvoiceRepo invoiceRepo;
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;

    private final EmailService emailService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var customer = customerRepo.findByEmailAndUser(invoiceDto.getCustomerEmail(), user)
                .orElseThrow(() -> new IllegalArgumentException("Customer does not exist"));
        invoiceDto.setCustomer(customer);

        var invoice = dtoToInvoice(invoiceDto, Invoice.builder().build());
        invoice.setUser(user);
        invoice = invoiceRepo.save(invoice);

        return invoiceToDto(invoice);
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var invoice = invoiceRepo.findByIdAndUser(invoiceDto.getId(), user)
                .orElseThrow(() -> new IllegalStateException("Invalid invoice number"));

        dtoToInvoice(invoiceDto, invoice);

        var savedInvoice = invoiceRepo.save(invoice);

        return invoiceToDto(savedInvoice);
    }

    @Override
    public List<InvoiceDto> getAllInvoice() {
        String email = getCurrentUserDetails().getEmail();
        return getInvoiceDtos(email);
    }

    @Override
    public List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize) {
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
        Sort descendingSort = Sort.by(Sort.Direction.DESC, "id");

        PageRequest pageable = PageRequest.of(pageNo, pageSize, descendingSort);

        return invoiceRepo.findAllInvoiceByUser(user, pageable)
                .map(this::invoiceToDto)
                .getContent();
    }

    @Override
    public List<InvoiceDto> getInvoiceDtos(String email) {
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        return invoiceRepo.findByUser(user).stream()
            .map(this::invoiceToDto)
            .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto getInvoiceById(String invoiceId) {
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        return invoiceRepo.findByIdAndUser(Long.valueOf(invoiceId), user)
                .map(this::invoiceToDto
                )
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice Id not found"));
    }

    @Override
    public GeneralResponse deleteInvoiceById(String invoiceId) {
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        invoiceRepo.deleteByIdAndUser(Long.valueOf(invoiceId), user);

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
        String email = getCurrentUserDetails().getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
        var message = "Your invoice attached to this mail is still out standing please pay up";
        invoiceRepo
                .findByUserAndIsInvoicePaidIsFalseAndLastReminderDateBefore(user, LocalDate.now())
                .forEach(invoice -> {
                    Customer customer = invoice.getCustomer();
                    emailService.send(customer.getFirstname(), message, customer.getEmail(),"INVOICE REMINDER");
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
