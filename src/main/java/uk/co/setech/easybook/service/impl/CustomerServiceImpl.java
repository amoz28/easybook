package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.CustomerDto;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.Customer;
import uk.co.setech.easybook.repository.CustomerRepo;
import uk.co.setech.easybook.repository.InvoiceRepo;
import uk.co.setech.easybook.service.CustomerService;
import uk.co.setech.easybook.utils.Utils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static uk.co.setech.easybook.utils.Utils.getCurrentUserDetails;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepo customerRepo;
    private final InvoiceRepo invoiceRepo;

    private final Supplier<CustomException> CUSTOMER_NOT_FOUND = () -> new CustomException(HttpStatus.NOT_FOUND, "Customer not found");

    @Override
    public GeneralResponse createCustomer(CustomerDto customerDto) {
        long userId = getCurrentUserDetails().getId();
        var customer = dtoToCustomer(customerDto, new Customer());
        customer.setUserId(userId);
        customerRepo.save(customer);

        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Customer Successfully Created")
                .build();
    }

    @Override
    public CustomerDto getCustomerByEmailAndUserId(String email, long userId) {
        return customerRepo.findByEmailAndUserId(email, userId)
                .map(this::customerToDto)
                .orElseThrow(CUSTOMER_NOT_FOUND);
    }

    @Override
    public CustomerDto getCustomerByIdAndUserId(long id, long userId) {
        return customerRepo.findByIdAndUserId(id, userId)
                .map(this::customerToDto)
                .orElseThrow(CUSTOMER_NOT_FOUND);
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
    public GeneralResponse updateCustomer(CustomerDto customerDto) {
        long userId = getCurrentUserDetails().getId();
        var customer = customerRepo.findByIdAndUserId(customerDto.getId(), userId)
                .orElseThrow(CUSTOMER_NOT_FOUND);

        dtoToCustomer(customerDto, customer);
        customerRepo.save(customer);

//        return customerToDto(savedCustomer);
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Customer Updated")
                .build();
    }

    @Override
    public GeneralResponse deleteCustomerByEmail(Long id) {
        try {
            long userId = getCurrentUserDetails().getId();
            var customer = customerRepo.findByIdAndUserId(id, userId)
                    .orElseThrow(CUSTOMER_NOT_FOUND);
            invoiceRepo.deleteByCustomerId(customer.getId());
            customerRepo.delete(customer);
        }catch (Exception e){
            return GeneralResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Deletion complete")
                    .build();
        }
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
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
