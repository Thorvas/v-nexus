package com.example.demo.Authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Class representing authentication request. It can be either used for login or registration attempts
 *
 * @author Thorvas
 */
@Data
public class AuthenticationRequest {

    @Size(min = 4, message = "Username should be at least 4 characters long.")
    @NotBlank(message = "Username should not be empty.")
    private String username;

    @Size(min = 4, message = "Password should be at least 4 characters long.")
    @NotBlank(message = "Password should not be empty.")
    private String password;
}
