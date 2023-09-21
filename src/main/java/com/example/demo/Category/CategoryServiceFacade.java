package com.example.demo.Category;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Volunteer.Volunteer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryServiceFacade {

    @Autowired
    private CategoryUtilityMapper categoryUtilityMapper;

    @Autowired
    CategoryAuthenticationManager categoryAuthenticationManager;

    public boolean checkIfAdmin(Volunteer volunteer) {

        return categoryAuthenticationManager.checkIfAdmin(volunteer);
    }

    public Volunteer getLoggedVolunteer() {

        return categoryAuthenticationManager.getLoggedVolunteer();
    }

    public CategoryDTO mapCategoryToDTO(Category category) {

        return categoryUtilityMapper.mapCategoryToDTO(category);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return categoryUtilityMapper.mapProjectToDTO(project);
    }

    public void mapDTOToCategory(CategoryDTO categoryDTO, Category category) {

        categoryUtilityMapper.mapDTOToCategory(categoryDTO, category);
    }
}
