package uk.co.setech.EasyBook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.dto.UserDto;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.model.User;
import uk.co.setech.EasyBook.service.CompanyService;
import uk.co.setech.EasyBook.utils.Utils;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private static final String USER_NOT_FOUND = "User with email: %s Not Found";

    private final UserRepo userRepo;

    @Override
    public UserDto getCompanyProfile() {
        return Utils.gerUserDetails();
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
        BeanUtils.copyProperties(user, userDto, Utils.getNullPropertyNames(user));
        return userDto;
    }

    private User dtoToCompany(UserDto userDto, User user) {
        BeanUtils.copyProperties(userDto, user, Utils.getNullPropertyNames(userDto));
        return user;
    }
}
