package com.example.demo.Services;

import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.UserData;
import com.example.demo.Repositories.UserDataRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsCustomImpl implements UserDetailsService {

    @Autowired
    private UserDataRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        UserData foundUser = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found."));
        return new CustomUserDetails(foundUser);
    }
}
