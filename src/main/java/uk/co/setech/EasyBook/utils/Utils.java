package uk.co.setech.EasyBook.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.co.setech.EasyBook.dto.UserDto;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>(pds.length);
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static UserDto getCurrentUserDetails() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDto userDto = UserDto.builder().build();
            BeanUtils.copyProperties(auth.getPrincipal(), userDto);
            return userDto;
        } catch (Exception e) {
            log.error("Error getting user principal details: {}", e.getMessage());
            throw new InsufficientAuthenticationException("Full authentication is required to access this resource");
        }
    }
}
