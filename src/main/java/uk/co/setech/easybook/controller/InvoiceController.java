package uk.co.setech.easybook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.easybook.dto.AuthenticationResponse;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.model.PaymentRequest;
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
    public ResponseEntity<List<InvoiceDto>> getAllInvoice(@RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(invoiceService.getAllInvoice(type));
    }

    @GetMapping("/pages")
    public ResponseEntity<List<InvoiceDto>> getAllInvoicesWithSize(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(value = "type", required = false) String type
    ) {
        return ResponseEntity.ok(invoiceService.getAllInvoicesWithSize(pageNo, pageSize, type));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceDto>> getOverdueInvoicesWithSize(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(invoiceService.getOverdueInvoicesWithSize(pageNo, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/userProfile")
    public ResponseEntity<AuthenticationResponse> getUserProfile() {
        return ResponseEntity.ok(invoiceService.getUserProfile());
    }

    @GetMapping("/byCustomerId/{customerId}")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByCustomerId(
            @RequestParam(value = "type", required = false) String type,@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getAllInvoiceByCustomerIdAndType(customerId, type));
    }

//    @PutMapping("/addPayment/{invoiceId}")
//    public ResponseEntity<GeneralResponse> addPayment( @PathVariable Long invoiceId) {
//        return ResponseEntity.ok(invoiceService.addPayment(invoiceId));
//    }

    @PutMapping("/addPayment/{invoiceId}")
    public ResponseEntity<GeneralResponse> addPayment( @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(invoiceService.addPayment(paymentRequest));
    }

    @PutMapping("/markAsSent/{invoiceId}")
    public ResponseEntity<GeneralResponse> markAsSent( @PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.markAsSent(invoiceId));
    }

    @GetMapping("/sendInvoice")
    public ResponseEntity<GeneralResponse> sendInvoice( @RequestParam(value = "invoiceId") Long invoiceId) {
        return ResponseEntity.ok(invoiceService.sendInvoice(invoiceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.deleteInvoiceById(id));
    }
}
