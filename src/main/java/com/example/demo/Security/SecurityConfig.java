package com.example.demo.Security;

import com.example.demo.Services.UserDetailsCustomImpl;
import com.example.demo.Utility.DelegatingAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration of project
 *
 * @author Thorvas
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsCustomImpl userDetailsService;

    @Autowired
    private DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity http) throws Exception {

        http.csrf((csrf) -> {
                    csrf.ignoringRequestMatchers(
                            AntPathRequestMatcher.antMatcher("/h2-console/**"));
                    csrf.disable();
                }
        );
        http.headers((headers) -> {
            headers.frameOptions((frameOption) -> frameOption.disable());
        });
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"),
                                AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(delegatingAuthenticationEntryPoint))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    public void configureIgnores(WebSecurity web) {
        web
                .ignoring()
                .requestMatchers("/h2-console/**");
    }
}
