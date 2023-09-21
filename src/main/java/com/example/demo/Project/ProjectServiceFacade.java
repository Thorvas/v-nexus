package com.example.demo.Project;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryService;
import com.example.demo.Opinion.Opinion;
import com.example.demo.Opinion.OpinionDTO;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectServiceFacade {

    @Autowired
    private ProjectUtilityMapper projectUtilityMapper;

    @Autowired
    private ProjectVolunteerManager projectVolunteerManager;

    @Autowired
    private CategoryService categoryService;

    public Category findCategory(Long categoryId) {

        return categoryService.findCategory(categoryId);
    }

    public boolean checkIfAdmin(Volunteer volunteer) {

        return projectVolunteerManager.checkIfAdmin(volunteer);
    }

    public Volunteer findVolunteer(Long volunteerId) {

        return projectVolunteerManager.findVolunteer(volunteerId);
    }

    public Volunteer getLoggedVolunteer() {

        return projectVolunteerManager.getLoggedVolunteer();
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return projectUtilityMapper.mapProjectToDTO(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return projectUtilityMapper.mapVolunteerToDTO(volunteer);
    }

    public OpinionDTO mapOpinionToDTO(Opinion opinion) {

        return projectUtilityMapper.mapOpinionToDTO(opinion);
    }

    public CategoryDTO mapCategoryToDTO(Category category) {

        return projectUtilityMapper.mapCategoryToDTO(category);
    }

    public void mapDTOToProject(ProjectDTO projectDTO, Project project) {

        projectUtilityMapper.mapDTOToProject(projectDTO, project);
    }
}
