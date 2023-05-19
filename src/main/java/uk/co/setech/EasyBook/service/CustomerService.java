package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.CustomerDto;
import uk.co.setech.EasyBook.dto.GeneralResponse;

import java.util.List;

public interface CustomerService {
    GeneralResponse createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerByEmail(String email);

    List<CustomerDto> getAllCustomers(int pageNo, int pageSize);

    List<CustomerDto> getAllCustomer();

    CustomerDto updateCustomer(CustomerDto customerDto);

    GeneralResponse deleteCustomerByEmail(String email);
}
