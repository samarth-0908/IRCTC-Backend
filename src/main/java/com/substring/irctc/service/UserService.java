package com.substring.irctc.service;

import com.substring.irctc.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

public interface UserService {

    public UserDto registerUser(UserDto userDto);


    public UserDto registerAdmin(UserDto userDto);
}
