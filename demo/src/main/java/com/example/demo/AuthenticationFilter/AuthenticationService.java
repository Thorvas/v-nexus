package com.example.demo.AuthenticationFilter;

import com.example.demo.DummyObject.ApiKey;
import com.example.demo.Repositories.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private ApiKeyRepository keyRepository;

    public Authentication getAuthentication(HttpServletRequest request) {

        String headerKey = request.getHeader("SECRET_API_KEY");
        Optional<ApiKey> apiKey = keyRepository.findByKey(headerKey);

        if (apiKey.isEmpty() || !(apiKey.get().getKey().equals(headerKey))) {

            throw new BadCredentialsException("Invalid API key");
        } else {

            return new ApiKeyAuthentication(apiKey.get().getKey(), apiKey.get().getAuthorities());
        }
    }
}
