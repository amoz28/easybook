package uk.co.setech.EasyBook.authenticated.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.authenticated.controller.UserDto;
import uk.co.setech.EasyBook.authenticated.dto.CustomerDto;
import uk.co.setech.EasyBook.authenticated.model.Customer;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.user.User;
import uk.co.setech.EasyBook.utils.ExcludeNullValues;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final UserRepo userRepo;
    private final ExcludeNullValues excludeNullValues;

    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    @Override
    public UserDto getCompanyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);

        return userDto;
    }

    @Override
    public UserDto updateCompanyProfile(UserDto userDto) {
        var user = userRepo.findByEmail(getCompanyProfile().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getCompanyProfile().getEmail())));
        User company = dtoToCompany(userDto, user);

        return companyToDto(userRepo.save(company));
    }

    private UserDto companyToDto(User user) {
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(user, userDto, excludeNullValues.getNullPropertyNames(user));
        return userDto;
    }

    private User dtoToCompany(UserDto userDto, User user) {
        BeanUtils.copyProperties(userDto, user, excludeNullValues.getNullPropertyNames(userDto));
        return user;
    }
}
