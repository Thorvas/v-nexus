package com.example.demo.Configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration containing beans for application
 *
 * @author Thorvas
 */
@Configuration
public class ProjectConfiguration {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        return modelMapper;
    }

}
