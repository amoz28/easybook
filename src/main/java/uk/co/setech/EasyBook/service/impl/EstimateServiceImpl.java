package uk.co.setech.EasyBook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.UserDto;
import uk.co.setech.EasyBook.dto.EstimateDto;
import uk.co.setech.EasyBook.model.Estimate;
import uk.co.setech.EasyBook.repository.CustomerRepo;
import uk.co.setech.EasyBook.repository.EstimateRepo;
import uk.co.setech.EasyBook.repository.InvoiceRepo;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.service.EstimateService;
import uk.co.setech.EasyBook.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstimateServiceImpl implements EstimateService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final InvoiceRepo invoiceRepo;
    private final CustomerRepo customerRepo;
    private final EstimateRepo estimateRepo;
    private final UserRepo userRepo;

    @Override
    public EstimateDto createEstimate(EstimateDto estimateDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var customer = customerRepo.findByEmailAndUser(estimateDto.getCustomerEmail(), user)
                .orElseThrow(()-> new IllegalArgumentException("Customer does not exist"));
        estimateDto.setCustomer(customer);

        var estimate = dtoToEstimate(estimateDto, Estimate.builder().build());

        estimate.setUser(user);
        estimate = estimateRepo.save(estimate);

        return estimateToDto(estimate, estimateDto);
    }


    @Override
    public EstimateDto updateEstimate(EstimateDto estimateDto) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        var estimate = estimateRepo.findByIdAndUser(estimateDto.getId(), user)
                .orElseThrow(()-> new IllegalStateException("Invalid invoice number"));

        estimate = dtoToEstimate(estimateDto, estimate);

        var savedInvoice = estimateRepo.save(estimate);

        return estimateToDto(savedInvoice, estimateDto);
    }

    @Override
    public List<EstimateDto> getAllEstimate() {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return estimateRepo.findByUser(user).stream()
            .map(estimate -> estimateToDto(estimate, EstimateDto.builder().build()))
            .collect(Collectors.toList());
    }

    @Override
    public EstimateDto getEstimateById(String invoiceId) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        return estimateRepo.findByIdAndUser(Long.valueOf(invoiceId), user)
                .map(estimate ->
                    estimateToDto(estimate, EstimateDto.builder().build())
                )
                .orElseThrow(()-> new IllegalArgumentException("Invoice Id not found"));
    }

    @Override
    public GeneralResponse deleteEstimateById(String invoiceId) {
        var user = userRepo.findByEmail(getUserDetails().getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getUserDetails().getEmail())));

        invoiceRepo.deleteByIdAndUser(Long.valueOf(invoiceId), user);

        return GeneralResponse.builder()
                .message("Estimate deleted")
                .build();
    }


    private EstimateDto estimateToDto(Estimate estimate, EstimateDto estimateDto) {
        BeanUtils.copyProperties(estimate, estimateDto);

        return estimateDto;
    }

    private Estimate dtoToEstimate(EstimateDto estimateDto, Estimate estimate) {
        BeanUtils.copyProperties(estimateDto, estimate, Utils.getNullPropertyNames(estimateDto));

        return estimate;
    }

    private UserDto getUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);
        return userDto;
    }
}
