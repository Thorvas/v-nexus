package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DummyObject.Category;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<CategoryDTO>> retrieveCategories() {

        List<CategoryDTO> categoryDTOs = categoryService.searchCategories().stream()
                .map(categoryMapper::mapCategoryToDTO)
                .collect(Collectors.toList());

        CollectionModel<CategoryDTO> resource = CollectionModel.of(categoryDTOs);

        resource.add(linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withSelfRel());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<CategoryDTO>> retrieveCategory(@PathVariable Long id) {

        Category foundCategory = categoryService.searchCategory(id).orElseThrow(() -> new EntityNotFoundException("Requested entity could not be found."));

        CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(foundCategory);

        EntityModel<CategoryDTO> resource = EntityModel.of(categoryDTO);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> retrieveProjects(@PathVariable Long id) {

        Category foundCategory = categoryService.searchCategory(id).orElseThrow(() -> new EntityNotFoundException("Requested category could not be found."));

        List<ProjectDTO> projectDTOs = foundCategory.getProjectsCategories().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs);

        resource.add(linkTo(methodOn(CategoryController.class)
                .retrieveProjects(id)).withSelfRel(),
                linkTo(methodOn(CategoryController.class)
                        .retrieveCategories()).withRel("root"));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
