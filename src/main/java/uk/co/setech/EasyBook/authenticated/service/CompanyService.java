package uk.co.setech.EasyBook.authenticated.service;

import uk.co.setech.EasyBook.authenticated.controller.UserDto;

public interface CompanyService {

    public UserDto getCompanyProfile();

    public UserDto updateCompanyProfile(UserDto userDto);
}
