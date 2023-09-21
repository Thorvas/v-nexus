package com.example.demo.Authentication;


import com.example.demo.User.UserDataRepository;
import com.example.demo.User.UserData;
import com.example.demo.User.UserRole;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Jwt.JwtService;
import com.example.demo.Volunteer.Volunteer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for authentication of user
 *
 * @author Thorvas
 */
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

    /**
     * Performs user registration
     *
     * @param request incoming request
     * @return JWT token
     */
    public Optional<AuthenticationResponse> register(AuthenticationRequest request) {

        Volunteer newVolunteer = new Volunteer();
        newVolunteer.setName("Blank");
        newVolunteer.setSurname("Blank");
        newVolunteer.setDateOfBirth(LocalDate.of(2000, 12, 12));
        newVolunteer.setContact("Blank");
        newVolunteer.setReputation(0);
        newVolunteer.setInterests(List.of("Blank"));

        volunteerRepository.save(newVolunteer);

        UserData userData = UserData.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_VOLUNTEER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .isActive(true)
                .referencedVolunteer(newVolunteer)
                .isAccountNonLocked(true)
                .build();

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {

            return Optional.empty();
        }

        userRepository.save(userData);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userData.getUsername());

        String jwtToken = jwtService.generateToken(userDetails);

        return Optional.of(AuthenticationResponse.builder()
                .token(jwtToken)
                .build());
    }

    /**
     * Performs user login
     *
     * @param request incoming request
     * @return JWT token
     */
    public Optional<AuthenticationResponse> login(AuthenticationRequest request) {

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (Exception e) {
            return Optional.empty();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        return Optional.of(AuthenticationResponse.builder()
                .token(jwtToken)
                .build());
    }

    /**
     * Checks whether volunteer is an admin
     *
     * @param volunteer Inspected volunteet
     * @return Boolean value containing result of operation
     */
    public boolean checkIfAdmin(Volunteer volunteer) {
        return volunteer.getUserData().isAdmin();
    }
}
