package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.setech.easybook.dto.InvoiceTemplateDto;
import uk.co.setech.easybook.service.InvoiceTemplateService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invoice/template")
public class InvoiceTemplateController {

    private final InvoiceTemplateService invoiceTemplateService;

    @GetMapping
    ResponseEntity<List<InvoiceTemplateDto>> getInvoiceTemplate() {
        List<InvoiceTemplateDto> invoiceTemplates = invoiceTemplateService.getAllInvoiceTemplates();
        return ResponseEntity.ok(invoiceTemplates);
    }
}
