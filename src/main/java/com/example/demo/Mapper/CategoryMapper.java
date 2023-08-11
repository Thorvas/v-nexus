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

/**
 * Mapper for categories
 *
 * @author Thorvas
 */
@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method that maps given category into its DTO with HATEOAS links
     *
     * @param categoryToMap Category that is to be mapped into DTO
     * @return Mapped categoryDTO object
     */
    public CategoryDTO mapCategoryToDTO(Category categoryToMap) {

        CategoryDTO newDTO = modelMapper.map(categoryToMap, CategoryDTO.class);

        Link allProjectsLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(categoryToMap.getId())).withRel("category-projects");

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategory(categoryToMap.getId())).withSelfRel();

        newDTO.add(allProjectsLink, selfLink);

        return newDTO;
    }
}
