package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.service.InvoiceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoice) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @PutMapping
    public ResponseEntity<InvoiceDto> updateInvoice(@RequestBody InvoiceDto invoice) {
        return ResponseEntity.ok(invoiceService.updateInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoice(@RequestParam("type") String type) {
        return ResponseEntity.ok(invoiceService.getAllInvoice(type));
    }

    @GetMapping("/pages")
    public ResponseEntity<List<InvoiceDto>> getAllInvoicesWithSize(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam("type") String type
    ) {
        return ResponseEntity.ok(invoiceService.getAllInvoicesWithSize(pageNo, pageSize, type));
    }

    @GetMapping("/byId")
    public ResponseEntity<InvoiceDto> getInvoice(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @GetMapping("/byEmail")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByCustomer(@RequestParam String email) {
        return ResponseEntity.ok(invoiceService.getAllInvoiceByCustomer(email));
    }

    @PutMapping("/addPayment")
    public ResponseEntity<GeneralResponse> addPayment(@RequestBody Long invoiceId) {
        return ResponseEntity.ok(invoiceService.addPayment(invoiceId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteInvoice(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(invoiceService.deleteInvoiceById(invoiceId));
    }
}
