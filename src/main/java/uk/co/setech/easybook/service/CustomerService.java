package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;

import java.util.List;

public interface CustomerService {
    GeneralResponse createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerByEmail(String email);

    List<CustomerDto> getAllCustomers(int pageNo, int pageSize);

    List<CustomerDto> getAllCustomer();

    CustomerDto updateCustomer(CustomerDto customerDto);

    GeneralResponse deleteCustomerByEmail(String email);
}
