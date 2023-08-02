package com.example.demo.Controller;

import com.example.demo.Objects.AuthenticationRequest;
import com.example.demo.Objects.AuthenticationResponse;
import com.example.demo.Services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request){

        AuthenticationResponse response = authenticationService.register(request).orElseThrow(() -> new IllegalArgumentException("User already exists!"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        AuthenticationResponse response = authenticationService.login(request).orElseThrow(() -> new BadCredentialsException("Provided credentials are incorrect."));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
