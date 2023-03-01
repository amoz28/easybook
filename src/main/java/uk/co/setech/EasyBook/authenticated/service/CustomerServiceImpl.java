package uk.co.setech.EasyBook.authenticated.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.dto.InvoiceDto;
import uk.co.setech.EasyBook.authenticated.model.Invoice;
import uk.co.setech.EasyBook.authenticated.repository.CustomerRepo;
import uk.co.setech.EasyBook.authenticated.model.Customer;
import uk.co.setech.EasyBook.utils.ExcludeNullValues;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;

    private final ExcludeNullValues excludeNullValues;
    /**
     * @param customerDto
     * @return
     */
    @Override
    public GeneralResponse createCustomer(CustomerDto customerDto) {
//        @TODO Validation on the data passed
        var customer = dtoToCustomer(customerDto, new Customer());
        customerRepo.save(customer);

        return GeneralResponse.builder()
                .message("User Created")
                .build();
    }

    /**
     * @param email
     * @return
     */
    @Override
    public CustomerDto getCustomerByEmail(String email) {
        return customerRepo.findByEmail(email)
                .map(this::customerToDto)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));


    }

    /**
     * @return
     */
    @Override
    public List<CustomerDto> getAllCustomer() {
        return customerRepo.findAll().stream()
                .map(this::customerToDto)
                .collect(Collectors.toList());
    }

    /**
     * @param customerDto
     * @return
     */
    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        var customer = customerRepo.findByEmail(customerDto.getEmail())
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        customer = dtoToCustomer(customerDto, customer);
        var savedCustomer = customerRepo.save(customer);

        return customerToDto(savedCustomer);
    }

    /**
     * @param email
     */
    @Override
    public GeneralResponse deleteCustomerByEmail(String email) {
        var customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        customerRepo.delete(customer);

        return GeneralResponse.builder()
                .message("Customer deleted")
                .build();
    }

    private CustomerDto customerToDto(Customer customer) {
        CustomerDto customerDto = CustomerDto.builder().build();
        BeanUtils.copyProperties(customer, customerDto, excludeNullValues.getNullPropertyNames(customer));

        return customerDto;
    }

    private Customer dtoToCustomer(CustomerDto customerDto, Customer customer) {
        BeanUtils.copyProperties(customerDto, customer, excludeNullValues.getNullPropertyNames(customerDto));

        return customer;
    }
}
