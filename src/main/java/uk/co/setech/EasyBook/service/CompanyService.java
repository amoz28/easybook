package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.UserDto;

public interface CompanyService {

    UserDto getCompanyProfile();

    UserDto updateCompanyProfile(UserDto userDto);
}
