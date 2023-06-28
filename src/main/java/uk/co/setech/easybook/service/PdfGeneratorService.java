package uk.co.setech.easybook.service;

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.model.Invoice;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(Invoice invoice) throws Exception {
        String htmlContent = generateHtmlContent(invoice);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.createPDF(outputStream);
        return outputStream.toByteArray();
    }

    private String generateHtmlContent(Invoice invoice) {
        // Generate the HTML content for the invoice using the invoice data
        // You can use libraries like Thymeleaf or FreeMarker to generate dynamic HTML templates
        return "<html><body><h1>Invoice</h1></body></html>";
    }
}
