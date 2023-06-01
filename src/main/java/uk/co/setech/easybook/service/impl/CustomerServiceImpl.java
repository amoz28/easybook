package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.repository.CustomerRepo;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.CustomerService;
import uk.co.setech.easybook.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;

    @Override
    public GeneralResponse createCustomer(CustomerDto customerDto) {
        long userId = getCurrentUserDetails().getId();
        var customer = dtoToCustomer(customerDto, new Customer());
        customer.setUserId(userId);
        customerRepo.save(customer);

        return GeneralResponse.builder()
                .message("User Successfully Created")
                .build();
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        long userId = getCurrentUserDetails().getId();

        return customerRepo.findByEmailAndUserId(email, userId)
                .map(this::customerToDto)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
    }

    @Override
    public CustomerDto getCustomerById(Integer id) {
        return customerRepo.findById(id)
                .map(this::customerToDto)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
    }

    @Override
    public List<CustomerDto> getAllCustomers(int pageNo, int pageSize) {
        long userId = getCurrentUserDetails().getId();
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        return customerRepo.findAllByUserId(userId, pageable)
                .map(this::customerToDto)
                .getContent();
    }

    @Override
    public List<CustomerDto> getAllCustomer() {
        long userId = getCurrentUserDetails().getId();
        return customerRepo.findAllByUserId(userId).stream()
                .map(this::customerToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        long userId = getCurrentUserDetails().getId();
        var customer = customerRepo.findByEmailAndUserId(customerDto.getEmail(), userId)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        dtoToCustomer(customerDto, customer);
        var savedCustomer = customerRepo.save(customer);

        return customerToDto(savedCustomer);
    }

    @Override
    public GeneralResponse deleteCustomerByEmail(String email) {
        long userId = getCurrentUserDetails().getId();
        var customer = customerRepo.findByEmailAndUserId(email, userId)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
        customerRepo.delete(customer);

        return GeneralResponse.builder()
                .message("Customer deleted")
                .build();
    }

    private CustomerDto customerToDto(Customer customer) {
        CustomerDto customerDto = CustomerDto.builder().build();
        BeanUtils.copyProperties(customer, customerDto, Utils.getNullPropertyNames(customer));
        return customerDto;
    }

    private Customer dtoToCustomer(CustomerDto customerDto, Customer customer) {
        BeanUtils.copyProperties(customerDto, customer, Utils.getNullPropertyNames(customerDto));
        return customer;
    }
}
