package com.example.demo.Mapper;

import com.example.demo.Controller.CategoryController;
import com.example.demo.Controller.ProjectController;
import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DummyObject.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO mapCategoryToDTO(Category categoryToMap) {

        CategoryDTO newDTO = modelMapper.map(categoryToMap, CategoryDTO.class);

        Link allProjectsLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(categoryToMap.getId())).withRel("all-category-projects");

        newDTO.add(categoryToMap.getProjectsCategories().stream()
                .map(project -> linkTo(methodOn(ProjectController.class)
                        .getProject(project.getId())).withRel("project-categories"))
                .collect(Collectors.toList()));

        newDTO.setProjectsCategories(categoryToMap.getProjectsCategories().stream()
                .map(project -> linkTo(methodOn(ProjectController.class)
                        .getProject(project.getId())).withRel("project-category"))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            list.add(allProjectsLink);
                            return list;
                        }
                )));

        return newDTO;
    }

    public static void mapPropertiesToCategory(Category sourceCategory, Category targetCategory) {

        targetCategory.setCategoryDescription(sourceCategory.getCategoryDescription());
        targetCategory.setCategoryName(sourceCategory.getCategoryName());
        targetCategory.setCategoryPopularity(sourceCategory.getCategoryPopularity());
        targetCategory.setProjectsCategories(sourceCategory.getProjectsCategories());

    }
}
