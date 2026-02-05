package com.university.administration.domain.dto;

public record AuthRequest(
        String userName,
        String password
) {}

