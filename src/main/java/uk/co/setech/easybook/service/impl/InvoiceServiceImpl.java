package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import uk.co.setech.easybook.dto.*;
import uk.co.setech.easybook.email.EmailSender;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.model.InvoiceItem;
import uk.co.setech.easybook.model.PaymentRequest;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.CustomerService;
import uk.co.setech.easybook.service.InvoiceService;
import uk.co.setech.easybook.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepo invoiceRepo;
    private final EmailSender emailService;
    private final CustomerService customerService;
    private final TemplateEngine templateEngine;

    private final UserRepo userRepo;

    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private static final String USER_ALREADY_EXIST = "User with email: %s Already Exists";

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        var user = getCurrentUserDetails();
        long userId = user.getId();
        var invoice = dtoToInvoice(invoiceDto, new Invoice());
//        invoice.setOutstandingBalance(invoice.getTotal());
        invoice.setUserId(userId);
        invoice = invoiceRepo.save(invoice);
        return invoiceToDto(invoice);
    }

    private String generateInvoiceHtml(Invoice invoice, UserDto user, CustomerDto customerDto) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("user", user);
        context.setVariable("customer", customerDto);
        return templateEngine.process("Invoice-template", context);
    }

    private byte[] generatePdfFromHtml(String htmlContent) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        renderer.finishPDF();
        return outputStream.toByteArray();
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        long userId = getCurrentUserDetails().getId();
        var invoice = invoiceRepo.findByIdAndUserId(invoiceDto.getId(), userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
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

    public List<InvoiceDto> getAllInvoiceByCustomerIdAndType(Long customerId, String type) {
        long userId = getCurrentUserDetails().getId();
        InvoiceType invoiceType = type == null ? null : InvoiceType.valueOf(type);

        List<Invoice> allUserInvoices = invoiceType == null
                ? invoiceRepo.findAllInvoiceByUserIdAndCustomerId(userId, customerId)
                : invoiceRepo.findAllInvoiceByUserIdAndCustomerIdAndType(userId, customerId, invoiceType);

        return allUserInvoices
                .stream()
                .map(this::invoiceToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize, String... type) {
        long userId = getCurrentUserDetails().getId();
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<Invoice> invoices = type == null
                ? invoiceRepo.findAllInvoiceByUserIdOrderByIdDesc(userId, pageable)
                : invoiceRepo.findAllInvoiceByUserIdAndTypeInOrderByIdDesc(userId, pageable, Stream.of(type).map(InvoiceType::valueOf).toArray(InvoiceType[]::new));
        return invoices
                .map(this::invoiceToDto)
                .getContent();
    }

    @Override
    public List<InvoiceDto> getOverdueInvoicesWithSize(int pageNo, int pageSize, String... type) {
        long userId = getCurrentUserDetails().getId();
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        Page<Invoice> invoices = invoiceRepo.findAllInvoiceByUserIdAndIsInvoicePaidOrderByDuedateDesc(userId, false, pageable);
        return invoices
                .stream()
                .map(this::invoiceToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthenticationResponse getUserProfile() {
        String email = getCurrentUserDetails().getEmail();
        System.out.println(getCurrentUserDetails());
        var user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var allInvoices = getOverdueAndPaidInvoice(user.getId(), InvoiceType.INVOICE);

//        var recentInvoice = invoiceService.getAllInvoicesWithSize(0,10, "INVOICE", "ESTIMATE");
        var recentInvoice = getOverdueInvoicesWithSize(0,10, "INVOICE", "ESTIMATE");

        var shortCutList = new ArrayList<InvoiceSummary>();

        shortCutList.add(
                InvoiceSummary.builder()
                        .title("Overdue Invoices")
                        .image("wallet")
                        .amount(allInvoices.getOverdueInvoiceTotal())
                        .build());

        shortCutList.add(
                InvoiceSummary.builder()
                        .title("Paid Invoices")
                        .image("wallet")
                        .amount(allInvoices.getPaidInvoiceTotal())
                        .build());

        return AuthenticationResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(email)
                .phoneNumber(user.getPhoneNumber())
                .address(user.getCompanyAddress())
                .postCode(user.getPostCode())
                .country(user.getCountry())
                .companyLogo(user.getCompanyLogo())
                .companyName(user.getCompanyName())
                .extraData(shortCutList)
                .recentInvoice(recentInvoice)
                .status(HttpStatus.OK.value())
                .build();
    }

    @Override
    public List<InvoiceDto> getInvoiceDtos(long userId, InvoiceType invoiceType) {
        List<Invoice> allUserInvoices = invoiceType == null
                ? invoiceRepo.findByUserIdOrderByIdDesc(userId)
                : invoiceRepo.findByUserIdAndTypeOrderByIdDesc(userId, invoiceType);
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
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    @Override
    public GeneralResponse deleteInvoiceById(long invoiceId) {
        long userId = getCurrentUserDetails().getId();
        invoiceRepo.deleteByIdAndUserId(invoiceId, userId);
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Invoice deleted")
                .build();
    }

    private InvoiceDto invoiceToDto(Invoice invoice) {
        var invoiceDto = new InvoiceDto();
        BeanUtils.copyProperties(invoice, invoiceDto);
        var invoiceItems = invoice.getItems().stream()
                .map(itemsDto -> ItemsDto.builder()
                        .id(Math.toIntExact(itemsDto.getId()))
                        .service(itemsDto.getService())
                        .description(itemsDto.getDescription())
                        .quantity(itemsDto.getQuantity())
                        .price(itemsDto.getPrice())
                        .build())
                .collect(Collectors.toList());
        invoiceDto.setItems(invoiceItems);
        return invoiceDto;
    }

    private Invoice dtoToInvoice(InvoiceDto invoiceDto, Invoice invoice) {
        BeanUtils.copyProperties(invoiceDto, invoice, Utils.getNullPropertyNames(invoiceDto));
        var invoiceItems = invoiceDto.getItems().stream()
                .map(itemsDto -> {
                    var invoiceItem = new InvoiceItem();
                    invoiceItem.setService(itemsDto.getService());
                    invoiceItem.setDescription(itemsDto.getDescription());
                    invoiceItem.setQuantity(itemsDto.getQuantity());
                    invoiceItem.setPrice(itemsDto.getPrice());
                    invoiceItem.setInvoice(invoice);
                    return invoiceItem;
                })
                .collect(Collectors.toList());
        invoice.setItems(invoiceItems);
        return invoice;
    }

    @Override
    public void sendInvoiceReminder() {
        var message = "Your invoice attached to this mail is still outstanding please pay up";
        invoiceRepo
                .findByIsInvoicePaidIsFalseAndLastReminderDateBefore(LocalDate.now())
                .forEach(invoice -> {
                    CustomerDto customer = customerService.getCustomerByIdAndUserId(invoice.getCustomerId(), invoice.getUserId());
                    emailService.send(customer.getFirstname(), customer.getEmail(), message, "INVOICE REMINDER");
                });
    }

    @Override
    public GeneralResponse addPayment(PaymentRequest paymentRequest) {
        var invoice = invoiceRepo.findById(paymentRequest.invoiceId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
        if((invoice.getAmountPaid() + paymentRequest.amountPaid())-invoice.getTotal() == 0){
            invoiceRepo.markInvoiceAsPaid(paymentRequest.invoiceId(), paymentRequest.paymentType(), paymentRequest.amountPaid());
        }else {
            invoiceRepo.markPartialPayment(paymentRequest.invoiceId(), paymentRequest.paymentType(), paymentRequest.amountPaid());
        }return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Payment was successfully updated")
                .build();
    }

    @Override
    public GeneralResponse markAsSent(Long invoiceId){
        var invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
        invoiceRepo.markInvoiceAsSent(invoiceId);
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Invoice was sent successfully")
                .build();
    }

    @Override
    public GeneralResponse sendInvoice(Long invoiceId) {
        var invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
        var customerId = invoice.getCustomerId();
        var user = getCurrentUserDetails();
        long userId = user.getId();
        var customer = customerService.getCustomerByIdAndUserId(customerId, userId);
        String htmlContent = generateInvoiceHtml(invoice, user, customer);
        try {
            log.info("Sending mail...");
            byte[] pdfBytes = generatePdfFromHtml(htmlContent);
            emailService.sendEmailWithAttachment(pdfBytes, customer.getFirstname(), customer.getEmail());
        } catch (Exception e) {
            log.error("Exception Occurred generating invoice ", e);
        }
        return GeneralResponse.builder()
                .message("Invoice has been resent")
                .build();
    }

    @Override
    public InvoicePaymentInfo getOverdueAndPaidInvoice(Long id, InvoiceType type) {
        return invoiceRepo.getOverdueAndPaidInvoice(id, type.toString());
    }

}