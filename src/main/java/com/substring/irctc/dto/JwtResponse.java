package com.substring.irctc.dto;

public record JwtResponse(
        String token,
        UserDto user
) {
}
