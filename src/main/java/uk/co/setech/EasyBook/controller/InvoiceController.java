package uk.co.setech.EasyBook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.dto.CustomerDto;
import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.InvoiceDto;
import uk.co.setech.EasyBook.service.InvoiceService;

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
    public ResponseEntity<List<InvoiceDto>> getAllInvoice() {
        return ResponseEntity.ok(invoiceService.getAllInvoice());
    }

    @GetMapping("/pages")
    public ResponseEntity<List<InvoiceDto>> getAllInvoicesWithSize(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(invoiceService.getAllInvoicesWithSize(pageNo, pageSize));
    }

    @GetMapping("/getInvoiceById")
    public ResponseEntity<InvoiceDto> getInvoice(@RequestParam String invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @PutMapping("/addPayment")
    public ResponseEntity<GeneralResponse> addPayment(@RequestBody Long invoiceId) {
        return ResponseEntity.ok(invoiceService.addPayment(invoiceId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteInvoice(@RequestParam String invoiceId) {
        return ResponseEntity.ok(invoiceService.deleteInvoiceById(invoiceId));
    }
}
