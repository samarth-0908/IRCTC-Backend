package com.substring.irctc.service.impl;

import com.substring.irctc.dto.UserDto;
import com.substring.irctc.entity.Role;
import com.substring.irctc.entity.User;
import com.substring.irctc.exceptions.ResourceNotFoundException;
import com.substring.irctc.repositories.RoleRepo;
import com.substring.irctc.repositories.UserRepo;
import com.substring.irctc.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class UserServiceImpl implements UserService {

    private RoleRepo roleRepo;

    private UserRepo userRepo;

    private ModelMapper modelMapper;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(RoleRepo roleRepo, UserRepo userRepo, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDto registerUser(UserDto userDto) {

        User user = modelMapper.map(userDto, User.class);

        Role role = roleRepo.findByName("ROLE_NORMAL").orElseThrow(() -> new ResourceNotFoundException("Server is not configured properly please contact support!"));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepo.save(user);

        return modelMapper.map(user, UserDto.class);


    }

    @Override
    public UserDto registerAdmin(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);

        Role roleAdmin = roleRepo.findByName("ROLE_ADMIN").orElseThrow(() -> new ResourceNotFoundException("Server is not configured properly please contact support!"));
        user.getRoles().add(roleAdmin);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepo.save(user);

        return modelMapper.map(user, UserDto.class);


    }
}
