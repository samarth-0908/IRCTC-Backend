package com.substring.irctc.dto;

import com.substring.irctc.entity.Booking;
import com.substring.irctc.entity.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;

    private String phone;

    private LocalDateTime createdAt;

    private List<Role> roles = new ArrayList<>();
}
