package uk.co.setech.EasyBook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.UserDto;
import uk.co.setech.EasyBook.dto.InvoiceDto;
import uk.co.setech.EasyBook.model.Invoice;
import uk.co.setech.EasyBook.repository.CustomerRepo;
import uk.co.setech.EasyBook.repository.InvoiceRepo;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.service.InvoiceService;
import uk.co.setech.EasyBook.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final InvoiceRepo invoiceRepo;
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(invoiceDto.getCustomerEmail(), user)
                .orElseThrow(()-> new IllegalArgumentException("Customer does not exist"));
        invoiceDto.setCustomer(customer);

        var invoice = dtoToInvoice(invoiceDto, Invoice.builder().build());
        invoice.setUser(user);
        invoice = invoiceRepo.save(invoice);

        return invoiceToDto(invoice, invoiceDto);
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var invoice = invoiceRepo.findByIdAndUser(invoiceDto.getId(), user)
                .orElseThrow(()-> new IllegalStateException("Invalid invoice number"));

        invoice = dtoToInvoice(invoiceDto, invoice);

        var savedInvoice = invoiceRepo.save(invoice);

        return invoiceToDto(savedInvoice, invoiceDto);
    }

    @Override
    public List<InvoiceDto> getAllInvoice() {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return invoiceRepo.findByUser(user).stream()
            .map(invoice -> invoiceToDto(invoice, InvoiceDto.builder().build()))
            .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto getInvoiceById(String invoiceId) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return invoiceRepo.findByIdAndUser(Long.valueOf(invoiceId), user)
                .map(invoice ->
                    invoiceToDto(invoice, InvoiceDto.builder().build())
                )
                .orElseThrow(()-> new IllegalArgumentException("Invoice Id not found"));
    }

    @Override
    public GeneralResponse deleteInvoiceById(String invoiceId) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        invoiceRepo.deleteByIdAndUser(Long.valueOf(invoiceId), user);

        return GeneralResponse.builder()
                .message("Invoice deleted")
                .build();
    }


    private InvoiceDto invoiceToDto(Invoice invoice, InvoiceDto invoiceDto) {
        BeanUtils.copyProperties(invoice, invoiceDto);

        return invoiceDto;
    }

    private Invoice dtoToInvoice(InvoiceDto invoiceDto, Invoice invoice) {
        BeanUtils.copyProperties(invoiceDto, invoice, Utils.getNullPropertyNames(invoiceDto));

        return invoice;
    }

    private UserDto getUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);
        return userDto;
    }

    @Override
    public void sendInvoiceReminder() {
//        invoiceRepo.deleteByIdAndUser();
    }
}
