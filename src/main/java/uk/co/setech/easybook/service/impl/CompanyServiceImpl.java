package uk.co.setech.easybook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.dto.UserDto;
import uk.co.setech.easybook.model.User;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.CompanyService;
import uk.co.setech.easybook.utils.Utils;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final UserRepo userRepo;

    @Override
    public UserDto getCompanyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(auth.getPrincipal(), userDto);
        return userDto;
    }

    @Override
    public UserDto updateCompanyProfile(UserDto userDto) {
        System.out.println("=============Got here==============");

        var user = userRepo.findByEmail(getCompanyProfile().getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, getCompanyProfile().getEmail())));
        System.out.println("Customer "+ userDto.getCompanyLogo());
        System.out.println("=============Done==============");
        User company = dtoToCompany(userDto, user);

        return companyToDto(userRepo.save(company));
    }

    private UserDto companyToDto(User user) {
        UserDto userDto = UserDto.builder().build();
        BeanUtils.copyProperties(user, userDto, Utils.getNullPropertyNames(user));
        return userDto;
    }

    private User dtoToCompany(UserDto userDto, User user) {
        BeanUtils.copyProperties(userDto, user, Utils.getNullPropertyNames(userDto));
        return user;
    }
}
