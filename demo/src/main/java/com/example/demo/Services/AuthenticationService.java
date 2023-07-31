package com.example.demo.Services;


import com.example.demo.Objects.*;
import com.example.demo.Repositories.UserDataRepository;
import com.example.demo.Repositories.VolunteerRepository;
import com.example.demo.Security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserDataRepository userRepository;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    VolunteerRepository volunteerRepository;

    @Autowired
    AuthenticationManager authManager;

    public AuthenticationResponse register(AuthenticationRequest request) {

        Volunteer newVolunteer = new Volunteer();

        volunteerRepository.save(newVolunteer);

        UserData userData = UserData.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_VOLUNTEER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .isActive(true)
                .isAccountNonLocked(true)
                .referencedVolunteer(newVolunteer)
                .build();

        userRepository.save(userData);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getUsername());

        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
