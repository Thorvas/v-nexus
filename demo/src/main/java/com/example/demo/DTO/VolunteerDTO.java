package com.example.demo.DTO;

import com.example.demo.DummyObject.Project;
import com.example.demo.DummyObject.Volunteer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class VolunteerDTO {

    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String contact;
    private List<String> skills;
    private Project project;
    private Integer reputation;
    private List<String> interests;

}
