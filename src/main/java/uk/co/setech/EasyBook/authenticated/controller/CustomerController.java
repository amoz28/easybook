package uk.co.setech.EasyBook.authenticated.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.service.CustomerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<GeneralResponse> createCustomer(@RequestBody CustomerDto customerDto){
        return ResponseEntity.ok(customerService.createCustomer(customerDto));
    }

    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerDto customerDto){
        return ResponseEntity.ok(customerService.updateCustomer(customerDto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomer(){
        return ResponseEntity.ok(customerService.getAllCustomer());
    }

    @GetMapping("/getCustomerByEmail")
    public ResponseEntity<CustomerDto> getCustomerDetails(@RequestParam String email){
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteCustomersDetails(@RequestParam String email){
        return ResponseEntity.ok(customerService.deleteCustomerByEmail(email));
    }
}
