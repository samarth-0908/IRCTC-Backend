package com.substring.irctc.controllers;

import com.substring.irctc.config.security.JwtHelper;
import com.substring.irctc.dto.ErrorResponse;
import com.substring.irctc.dto.JwtResponse;
import com.substring.irctc.dto.LoginRequest;
import com.substring.irctc.dto.UserDto;
import com.substring.irctc.entity.User;
import com.substring.irctc.repositories.UserRepo;
import com.substring.irctc.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtHelper jwtHelper;
    private UserService userService;
    private UserRepo userRepo;
    private ModelMapper modelMapper;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtHelper jwtHelper, UserService userService, UserRepo userRepo, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.userService = userService;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    //
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {


        ///token generation code

    try {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.username(),loginRequest.password());

        this.authenticationManager.authenticate(authentication);

        //generate token

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
        String token = this.jwtHelper.generateToken(userDetails);
        User user = userRepo.findByEmail(loginRequest.username()).get();
        JwtResponse jwtResponse = new JwtResponse(
                token,
                modelMapper.map(user, UserDto.class)
        );
        //cookie mein token ko bhej sakte hai

        return new ResponseEntity<>(jwtResponse, org.springframework.http.HttpStatus.OK);

    }

    catch (BadCredentialsException ex) {
        System.out.println("Invalid Credentials");
        ErrorResponse errorResponse = new ErrorResponse(
                "The username or password you entered is incorrect.",
                "403",
                false
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    }


    //register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserDto userDto
            ) {

        UserDto userDto1 = userService.registerUser(userDto);

        return new ResponseEntity<>(userDto1,HttpStatus.CREATED);
    }



    //register new admin

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(
            @Valid @RequestBody UserDto userDto
    ) {
        UserDto userDto1 = userService.registerAdmin(userDto);

        return new ResponseEntity<>(userDto1,HttpStatus.CREATED);

    }
    @GetMapping("/test")
    @PreAuthorize("hasRole('NORMAL')")
    public String test() {
        return "test";
    }

}
