package uk.co.setech.easybook.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.dto.ItemsDto;
import uk.co.setech.easybook.dto.UserDto;
import uk.co.setech.easybook.email.EmailService;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.model.InvoiceItem;
import uk.co.setech.easybook.model.User;
import uk.co.setech.easybook.repository.CustomerRepo;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.InvoiceService;
import uk.co.setech.easybook.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final InvoiceRepo invoiceRepo;
    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;

    private final EmailService emailService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(invoiceDto.getCustomerEmail(), user)
                .orElseThrow(() -> new IllegalArgumentException("Customer does not exist"));
        invoiceDto.setCustomer(customer);

        var invoice = dtoToInvoice(invoiceDto, Invoice.builder().build());
        invoice.setUser(user);
        invoice = invoiceRepo.save(invoice);

        try {
            // Generate the invoice as a PDF
            byte[] invoicePdf = generateInvoicePdf(invoice, user, customer);

            // Send the email with the invoice as an attachment
            emailService.sendEmailWithAttachment(invoicePdf,customer.getFirstname(), customer.getEmail());

        } catch (Exception e) {
            log.error("Exception Occurred generating invoice ", e);
        }
        return invoiceToDto(invoice);
    }

    private byte[] generateInvoicePdf(Invoice invoice, User user, Customer customer) throws DocumentException, IOException {
        // Create a new PDF document
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        // Open the PDF document
        document.open();

        // Add content to the PDF
        byte[] imageBytes = Base64.getDecoder().decode(user.getCompanyLogo());
        Image logo = Image.getInstance(imageBytes);
        logo.scaleToFit(150, 150);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        // Add sender details
        Font senderFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA, 16);
        Paragraph senderDetails = new Paragraph();
        senderDetails.add(new Paragraph(user.getCompanyName(), companyFont));
        senderDetails.add(new Paragraph("Address: "+user.getCompanyAddress(), senderFont));
        senderDetails.add(new Paragraph(user.getCity(), senderFont));
        senderDetails.add(new Paragraph(user.getPostCode(), senderFont));
        senderDetails.add(new Paragraph(user.getPhoneNumber(), senderFont));
        senderDetails.add(new Paragraph(user.getEmail(), senderFont));
        senderDetails.setAlignment(Element.ALIGN_CENTER);
        document.add(senderDetails);

        // Add invoice number
        Font invoiceNumberFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph invoiceNumber = new Paragraph("Invoice No.: INV-00"+customer.getId()+"/"+invoice.getId(), invoiceNumberFont);
        invoiceNumber.setAlignment(Element.ALIGN_RIGHT);
        document.add(invoiceNumber);

        // Add receiver details
        Font receiverFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph receiverDetails = new Paragraph();
        receiverDetails.add(new Paragraph(customer.getFirstname()+" "+customer.getLastname(), receiverFont));
        receiverDetails.add(new Paragraph(customer.getAddress(), receiverFont));
        receiverDetails.add(new Paragraph(customer.getPostcode(), receiverFont));
        receiverDetails.add(new Paragraph(customer.getCountry(), receiverFont));
        receiverDetails.add(new Paragraph(customer.getPhonenumber(), receiverFont));
        receiverDetails.add(new Paragraph(customer.getEmail(), receiverFont));
        receiverDetails.setAlignment(Element.ALIGN_LEFT);
        document.add(receiverDetails);

        // Add invoice details
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add invoice table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);
        table.setSpacingAfter(20f);

        PdfPCell cell1 = new PdfPCell(new Phrase("S/N"));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Description"));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("Unit Price"));
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase("Quantity"));
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell4);

        PdfPCell cell5 = new PdfPCell(new Phrase("Total"));
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell5);

        int serialNumber = 1;
        for (var invoiceItem : invoice.getItems()) {
            table.addCell(String.valueOf(serialNumber++));
            table.addCell(invoiceItem.getDescription());
            table.addCell(String.valueOf(invoiceItem.getPrice()));
            table.addCell(String.valueOf(invoiceItem.getQuantity()));
            table.addCell(String.valueOf(invoiceItem.getPrice()));
        }

        document.add(table);

        // Add invoice summary
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(40);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setSpacingBefore(20f);

        PdfPCell subTotalCell = new PdfPCell(new Phrase("Sub Total:", summaryFont));
        subTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(subTotalCell);

        PdfPCell subTotalValueCell = new PdfPCell(new Phrase(String.valueOf(invoice.getSubtotal()), summaryFont));
        subTotalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(subTotalValueCell);

        PdfPCell vatCell = new PdfPCell(new Phrase("VAT (20%):", summaryFont));
        vatCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(vatCell);

        PdfPCell vatValueCell = new PdfPCell(new Phrase(invoice.getVat(), summaryFont));
        vatValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(vatValueCell);

        PdfPCell totalCell = new PdfPCell(new Phrase("Total:", summaryFont));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(totalCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.valueOf(invoice.getTotal()), summaryFont));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.addCell(totalValueCell);

        document.add(summaryTable);

        // Close the PDF document
        document.close();

        return outputStream.toByteArray();
    }

    @Override
    public InvoiceDto updateInvoice(InvoiceDto invoiceDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var invoice = invoiceRepo.findByIdAndUser(invoiceDto.getId(), user)
                .orElseThrow(() -> new IllegalStateException("Invalid invoice number"));

        invoice = dtoToInvoice(invoiceDto, invoice);

        var savedInvoice = invoiceRepo.save(invoice);

        return invoiceToDto(savedInvoice);
    }

    @Override
    public List<InvoiceDto> getAllInvoice() {
        String email = getUserDetails().getEmail();
        return getInvoiceDtos(email);
    }

    @Override
    public List<InvoiceDto> getAllInvoiceByCustomer(String email) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(email, user)
                .orElseThrow(() -> new IllegalArgumentException("Customer does not exist"));

        return invoiceRepo.findAllInvoiceByUserAndCustomer(user, customer)
                .stream()
                .map(this::invoiceToDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<InvoiceDto> getAllInvoicesWithSize(int pageNo, int pageSize) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));
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
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return invoiceRepo.findByIdAndUser(Long.valueOf(invoiceId), user)
                .map(this::invoiceToDto
                )
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice Id not found"));
    }

    @Override
    public GeneralResponse deleteInvoiceById(String invoiceId) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        invoiceRepo.deleteByIdAndUser(Long.valueOf(invoiceId), user);

        return GeneralResponse.builder()
                .message("Invoice deleted")
                .build();
    }


    private InvoiceDto invoiceToDto(Invoice invoice) {
        var invoiceDto = InvoiceDto.builder().build();
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
                    invoiceItem.setQuantity(itemsDto.getQuantity()==null?itemsDto.getQuantity(): 1);
                    invoiceItem.setPrice(itemsDto.getPrice());
                    invoiceItem.setInvoice(invoice);
                    return invoiceItem;
                })
                .collect(Collectors.toList());
        invoice.setItems(invoiceItems);
        return invoice;
    }

    private UserDto getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);
        return userDto;
    }

    @Override
    public void sendInvoiceReminder() {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));
        var message = "Your invoice attached to this mail is still out standing please pay up";
        invoiceRepo
                .findByUserAndIsInvoicePaidIsFalseAndLastReminderDateBefore(user, LocalDate.now())
                .stream()
                .map(invoice -> {
                    emailService
                            .send(invoice.getCustomer().getFirstname(), message, invoice.getCustomer().getEmail(), "INVOICE REMINDER");
                    return null;
                })
                .collect(Collectors.toList());
    }

    @Override
    public GeneralResponse addPayment(Long invoiceId) {
        invoiceRepo.findById(invoiceId)
                .orElseThrow(()->new IllegalStateException("Invoice not found"));
        return GeneralResponse.builder()
                .message("Payment was successfuly updated")
                .build();
    }
}
