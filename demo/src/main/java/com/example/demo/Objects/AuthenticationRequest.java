package com.example.demo.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @Size(min = 4, message = "Username should be at least 4 characters long.")
    @NotBlank(message = "Username should not be empty.")
    private String username;

    @Size(min = 4, message = "Password should be at least 4 characters long.")
    @NotBlank(message = "Password should not be empty.")
    private String password;
}
