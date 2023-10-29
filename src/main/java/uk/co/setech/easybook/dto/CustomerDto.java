package uk.co.setech.easybook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.repository.CustomerRepo;
import uk.co.setech.easybook.utils.Utils;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
//    @Autowired
//    private CustomerRepo customerRepo;

    private Long id;
    private String displayname;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String address;
    private String companyname;
    private String taxNumber;
    private String postcode;
    private String city;
    private String county;
    private String country;
//
//    public GeneralResponse createCustomer(CustomerDto customerDto) {
//        long userId = getCurrentUserDetails().getId();
//        var customer = dtoToCustomer(customerDto, new Customer());
//        customer.setUserId(userId);
//        customerRepo.save(customer);
//
//        return GeneralResponse.builder()
//                .status(HttpStatus.OK.value())
//                .message("Customer Successfully Created")
//                .build();
//    }
//
//    private Customer dtoToCustomer(CustomerDto customerDto, Customer customer) {
//        BeanUtils.copyProperties(customerDto, customer, Utils.getNullPropertyNames(customerDto));
//        return customer;
//    }
//
//    private CustomerDto customerToDto(Customer customer) {
//        CustomerDto customerDto = CustomerDto.builder().build();
//        BeanUtils.copyProperties(customer, customerDto, Utils.getNullPropertyNames(customer));
//        return customerDto;
//    }
}
