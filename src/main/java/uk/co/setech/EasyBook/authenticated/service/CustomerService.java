package uk.co.setech.EasyBook.authenticated.service;

import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    GeneralResponse createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerByEmail(String email);

    List<CustomerDto> getAllCustomer();

    CustomerDto updateCustomer(CustomerDto customerDto);

    GeneralResponse deleteCustomerByEmail(String email);
}
