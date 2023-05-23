package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.service.CustomerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<GeneralResponse> createCustomer(
            @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.ok(customerService.createCustomer(customerDto));
    }

    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomer(
            @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(customerDto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomer() {
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    @GetMapping("/pages")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageNo, pageSize));
    }

    @GetMapping("/getCustomerByEmail")
    public ResponseEntity<CustomerDto> getCustomerDetails(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteCustomersDetails(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(customerService.deleteCustomerByEmail(email));
    }
}
