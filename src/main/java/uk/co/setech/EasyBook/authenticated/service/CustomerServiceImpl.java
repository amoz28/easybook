package uk.co.setech.EasyBook.authenticated.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.authenticated.controller.UserDto;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.repository.CustomerRepo;
import uk.co.setech.EasyBook.authenticated.model.Customer;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.utils.ExcludeNullValues;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final ExcludeNullValues excludeNullValues;
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    @Override
    public GeneralResponse createCustomer(CustomerDto customerDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = dtoToCustomer(customerDto, new Customer());
        customer.setUser(user);
        customerRepo.save(customer);

        return GeneralResponse.builder()
                .message("User Created")
                .build();
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return customerRepo.findByEmailAndUser(email, user)
                .map(this::customerToDto)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
    }
    public List<CustomerDto> getAllCustomers(int pageNo, int pageSize) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        return customerRepo.findAllByUser(user, pageable)
                .map(this::customerToDto)
                .getContent();
    }

    @Override
    public List<CustomerDto> getAllCustomer() {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));
        
        return customerRepo.findAllByUser(user).stream()
                .map(this::customerToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(customerDto.getEmail(), user)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        customer = dtoToCustomer(customerDto, customer);
        var savedCustomer = customerRepo.save(customer);

        return customerToDto(savedCustomer);
    }

    @Override
    public GeneralResponse deleteCustomerByEmail(String email) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(email, user)
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

    private UserDto getUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);
        return userDto;
    }
}
