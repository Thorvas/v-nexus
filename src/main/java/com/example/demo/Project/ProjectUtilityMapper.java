package com.example.demo.Project;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryMapper;
import com.example.demo.Opinion.Opinion;
import com.example.demo.Opinion.OpinionDTO;
import com.example.demo.Opinion.OpinionMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectUtilityMapper {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private OpinionMapper opinionMapper;



    public ProjectDTO mapProjectToDTO(Project project) {

        return projectMapper.mapProjectToDTO(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return volunteerMapper.mapVolunteerToDTO(volunteer);
    }

    public OpinionDTO mapOpinionToDTO(Opinion opinion) {

        return opinionMapper.mapOpinionToDTO(opinion);
    }

    public CategoryDTO mapCategoryToDTO(Category category) {

        return categoryMapper.mapCategoryToDTO(category);
    }

    public void mapDTOToProject(ProjectDTO projectDTO, Project project) {

        modelMapper.map(projectDTO, project);
    }
}
