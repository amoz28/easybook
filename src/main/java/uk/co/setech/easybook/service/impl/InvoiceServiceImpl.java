package uk.co.setech.easybook.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.dto.ItemsDto;
import uk.co.setech.easybook.dto.UserDto;
import uk.co.setech.easybook.email.EmailService;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Invoice;
import uk.co.setech.easybook.model.InvoiceItem;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.service.CustomerService;
import uk.co.setech.easybook.service.InvoiceService;
import uk.co.setech.easybook.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepo invoiceRepo;
    private final EmailService emailService;
    private final CustomerService customerService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        var user = getCurrentUserDetails();
        long userId = user.getId();
        Integer customerId = invoiceDto.getCustomerId();
        var customer = customerService.getCustomerByIdAndUserId(customerId, userId);
        var invoice = dtoToInvoice(invoiceDto, new Invoice());
        invoice.setUserId(userId);
        invoice = invoiceRepo.save(invoice);
        try {
            byte[] invoicePdf = generateInvoicePdf(invoice, user, customer);
            emailService.sendEmailWithAttachment(invoicePdf, customer.getFirstname(), customer.getEmail());
        } catch (Exception e) {
            log.error("Exception Occurred generating invoice ", e);
        }
        return invoiceToDto(invoice);
    }

    private byte[] generateInvoicePdf(Invoice invoice, UserDto user, CustomerDto customer) throws DocumentException, IOException {
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

        PdfPCell vatValueCell = new PdfPCell(new Phrase(String.valueOf(invoice.getVat()), summaryFont));
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

    public List<InvoiceDto> getAllInvoiceByCustomerEmail(String email) {
        long userId = getCurrentUserDetails().getId();
        var customer = customerService.getCustomerByEmailAndUserId(email, userId);
        return invoiceRepo.findAllInvoiceByUserIdAndCustomerId(userId, customer.getId())
                .stream()
                .map(this::invoiceToDto)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
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
    public GeneralResponse addPayment(Long invoiceId) {
        invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invoice not found"));
        return GeneralResponse.builder()
                .message("Payment was successfully updated")
                .build();
    }
}
