package uk.co.setech.EasyBook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.dto.UserDto;
import uk.co.setech.EasyBook.service.CompanyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class UserController {

    private final CompanyService companyService;
    @GetMapping()
    public ResponseEntity<UserDto> getCompanyProfile(){
        return ResponseEntity.ok(companyService.getCompanyProfile());
    }

    @PutMapping()
    public ResponseEntity<UserDto> updateUserDetails(@RequestBody UserDto userDto){
        return ResponseEntity.ok(companyService.updateCompanyProfile(userDto));
    }
}