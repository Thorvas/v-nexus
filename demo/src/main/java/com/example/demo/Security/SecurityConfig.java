package com.example.demo.Security;

import com.example.demo.AuthenticationFilter.CustomAuthenticationFilter;
import com.example.demo.Services.UserDetailsCustomImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsCustomImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public FilterRegistrationBean<CustomAuthenticationFilter> myFilter(CustomAuthenticationFilter filter) {

        FilterRegistrationBean<CustomAuthenticationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/postEstimation/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

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
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .anyRequest().permitAll());

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
