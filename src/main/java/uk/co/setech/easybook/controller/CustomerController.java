package uk.co.setech.easybook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.service.CustomerService;

import java.util.List;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

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
        Integer userId = getCurrentUserDetails().getId();
        return ResponseEntity.ok(customerService.getCustomerByEmailAndUserId(email, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerDetails(
            @PathVariable Long id
    ) {
        Integer userId = getCurrentUserDetails().getId();
        return ResponseEntity.ok(customerService.getCustomerByIdAndUserId(id, userId));
    }

    @DeleteMapping
    public ResponseEntity<GeneralResponse> deleteCustomersDetails(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(customerService.deleteCustomerByEmail(email));
    }
}
