package com.codecollab.auth;

public record RegisterRequest(String name, String email, String password, String role) {}
