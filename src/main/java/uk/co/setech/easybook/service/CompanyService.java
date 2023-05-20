package uk.co.setech.easybook.service;

import uk.co.setech.easybook.dto.UserDto;

public interface CompanyService {

    UserDto getCompanyProfile();

    UserDto updateCompanyProfile(UserDto userDto);
}
