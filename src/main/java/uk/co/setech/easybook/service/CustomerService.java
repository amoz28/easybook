package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;

import java.util.List;

public interface CustomerService {
    GeneralResponse createCustomer(CustomerDto customerDto);

    CustomerDto getCustomerByEmailAndUserId(String email, long userId);

    CustomerDto getCustomerByIdAndUserId(long id, long userId);

    List<CustomerDto> getAllCustomers(int pageNo, int pageSize);

    List<CustomerDto> getAllCustomer();

    GeneralResponse updateCustomer(CustomerDto customerDto);

    GeneralResponse deleteCustomerByEmail(Long id);
}
