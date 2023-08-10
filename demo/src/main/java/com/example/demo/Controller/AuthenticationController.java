package com.example.demo.Controller;

import com.example.demo.Objects.AuthenticationRequest;
import com.example.demo.Objects.AuthenticationResponse;
import com.example.demo.Services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Authentication controller that verifies registration and login data
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    private final String USER_EXISTS_ERROR = "User already exists.";
    private final String INCORRECT_CREDENTIALS_ERROR = "Provided credentials are incorrect.";

    /**
     * Registration endpoint. Creates new user based on login and password credentials and returns generated JWT token
     * Passwords are encrypted in BCrypt
     *
     * @param request request object containing login and password
     * @return created JWT token based on provided credentials
     */
    @PostMapping(value = "/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody AuthenticationRequest request) {

        AuthenticationResponse response = authenticationService.register(request).orElseThrow(() -> new IllegalArgumentException(USER_EXISTS_ERROR));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Login endpoint. Retrieves JWT token based on credentials included in request
     *
     * @param request request object containing login and password
     * @return created JWT token based on provided credentials
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {

        AuthenticationResponse response = authenticationService.login(request).orElseThrow(() -> new BadCredentialsException(INCORRECT_CREDENTIALS_ERROR));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
