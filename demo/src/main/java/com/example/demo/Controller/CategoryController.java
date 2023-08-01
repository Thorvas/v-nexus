package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Objects.Category;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.CategoryService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private VolunteerService volunteerService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> postCategory(@RequestBody Category category, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        if (loggedUser.getUserData().isAdmin()) {

            Category savedCategory = categoryService.createCategory(category.getCategoryName(), category.getCategoryDescription(), category.getCategoryPopularity());
            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(savedCategory);

            return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
        } else {

            throw new BadCredentialsException("You are not permitted to add new categories.");
        }
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody Category category,
                                                      @PathVariable Long id,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException("Category could not be found."));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        if (loggedUser.getUserData().isAdmin()) {

            Category savedCategory = categoryService.updateCategory(foundCategory, category);
            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(savedCategory);

            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        } else {

            throw new BadCredentialsException("You are not permitted to update categories.");
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<CategoryDTO>> retrieveCategories() {

        List<CategoryDTO> categoryDTOs = categoryService.searchCategories().stream()
                .map(categoryMapper::mapCategoryToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withSelfRel();

        CollectionModel<CategoryDTO> resource = CollectionModel.of(categoryDTOs, selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> retrieveCategory(@PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException("Requested entity could not be found."));

        CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(foundCategory);

        Link rootLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel("root");

        categoryDTO.add(rootLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> retrieveProjects(@PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException("Requested category could not be found."));

        List<ProjectDTO> projectDTOs = foundCategory.getProjectsCategories().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel("root");

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs);

        resource.add(selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
