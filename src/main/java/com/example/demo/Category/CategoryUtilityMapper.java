package com.example.demo.Category;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryUtilityMapper {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO mapCategoryToDTO(Category category) {

        return categoryMapper.mapCategoryToDTO(category);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return projectMapper.mapProjectToDTO(project);
    }

    public void mapDTOToCategory(CategoryDTO categoryDTO, Category category) {

        modelMapper.map(categoryDTO, category);
    }
}
