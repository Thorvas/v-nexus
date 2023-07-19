package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import java.util.List;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class CategoryDTO {

    private Long id;
    private String categoryName;
    private String categoryDescription;
    private Integer categoryPopularity;
    private List<ProjectDTO> projectsCategories;
}
