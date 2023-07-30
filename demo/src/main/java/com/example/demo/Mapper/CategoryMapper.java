package com.example.demo.Mapper;

import com.example.demo.Controller.CategoryController;
import com.example.demo.DTO.CategoryDTO;
import com.example.demo.Objects.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO mapCategoryToDTO(Category categoryToMap) {

        CategoryDTO newDTO = modelMapper.map(categoryToMap, CategoryDTO.class);

        Link allProjectsLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(categoryToMap.getId())).withRel("category-projects");

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategory(categoryToMap.getId())).withSelfRel();

        Link rootLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel("root");

        newDTO.add(allProjectsLink, selfLink, rootLink);

        return newDTO;
    }
}
