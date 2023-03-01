package uk.co.setech.EasyBook.authenticated.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.dto.InvoiceDto;
import uk.co.setech.EasyBook.authenticated.model.Invoice;
import uk.co.setech.EasyBook.authenticated.service.CustomerService;
import uk.co.setech.EasyBook.authenticated.service.InvoiceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoice){
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @PutMapping
    public ResponseEntity<InvoiceDto> updateInvoice(@RequestBody InvoiceDto invoice){
        return ResponseEntity.ok(invoiceService.updateInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getAllInvoice(){
        return ResponseEntity.ok(invoiceService.getAllInvoice());
    }

    @GetMapping("/getInvoiceById")
    public ResponseEntity<InvoiceDto> getInvoice(@RequestParam String invoiceId){
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteInvoice(@RequestParam String invoiceId){
        return ResponseEntity.ok(invoiceService.deleteInvoiceById(invoiceId));
    }
}
