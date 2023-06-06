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

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/byCustomerId")
    public ResponseEntity<List<InvoiceDto>> getInvoiceByCustomer(
            @RequestParam(value = "type", required = false) String type,@RequestParam Long customerId) {
        return ResponseEntity.ok(invoiceService.getAllInvoiceByCustomerIdAndType(customerId, type));
    }

    @PutMapping("/addPayment")
    public ResponseEntity<GeneralResponse> addPayment( @RequestParam(value = "invoiceId") Long invoiceId) {
        return ResponseEntity.ok(invoiceService.addPayment(invoiceId));
    }

    @GetMapping("/resendInvoice")
    public ResponseEntity<GeneralResponse> resendInvoice( @RequestParam(value = "invoiceId") Long invoiceId) {
        return ResponseEntity.ok(invoiceService.resendInvoice(invoiceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.deleteInvoiceById(id));
    }
}
