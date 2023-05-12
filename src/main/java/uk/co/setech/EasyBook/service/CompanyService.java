package uk.co.setech.EasyBook.service;

import uk.co.setech.EasyBook.dto.UserDto;

public interface CompanyService {

    public UserDto getCompanyProfile();

    public UserDto updateCompanyProfile(UserDto userDto);
}
