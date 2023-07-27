package com.example.demo.Services;

import com.example.demo.DummyObject.CustomUserDetails;
import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Repositories.VolunteerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsCustomImpl implements UserDetailsService {

    @Autowired
    private VolunteerRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Volunteer foundUser = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));
        return new CustomUserDetails(foundUser);
    }
}
