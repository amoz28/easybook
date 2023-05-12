package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    GeneralResponse createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerByEmail(String email);

    List<CustomerDto> getAllCustomer();

    CustomerDto updateCustomer(CustomerDto customerDto);

    GeneralResponse deleteCustomerByEmail(String email);
}
