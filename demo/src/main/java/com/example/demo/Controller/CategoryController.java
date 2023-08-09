package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Objects.Category;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.AuthenticationService;
import com.example.demo.Services.CategoryService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for categories
 * Categories can be modified and created only by administrators but retrieval is also allowed for regular authenticated users
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";
    private final String CATEGORY_NOT_FOUND_MESSAGE = "Requested category could not be found.";
    private final String ROOT_LINK = "root";

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private VolunteerService volunteerService;

    /**
     * POST endpoint for categories. Allows administrators to create new categories for project matching
     *
     * @param category passed JSON category that will be saved within database
     * @return JSON response containing created category
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> postCategory(@Valid @RequestBody CategoryDTO category) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser)) {

            Category savedCategory = categoryService.createCategory(category.getCategoryName(), category.getCategoryDescription(), category.getCategoryPopularity());
            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(savedCategory);

            return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
        } else {

            throw new BadCredentialsException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * PATCH endpoint for categories. Allows administrators to modify existing categories.
     *
     * @param category passed JSON category that will replace its' existing counterpart
     * @param id       Long id value of updated category
     * @return JSON response containing updated category
     */
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO category,
                                                      @PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser)) {

            Category savedCategory = categoryService.updateCategory(foundCategory, category);
            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(savedCategory);

            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        } else {

            throw new BadCredentialsException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * DELETE endpoint for categories. Allows administrators to delete certain categories
     *
     * @param id Long id value of deleted category
     * @return JSON response containing deleted category
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser)) {

            categoryService.deleteCategory(foundCategory);

            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(foundCategory);
            Link selfLink = linkTo(methodOn(CategoryController.class).deleteCategory(id))
                    .withSelfRel();

            categoryDTO.add(selfLink);

            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
        } else {

            throw new BadCredentialsException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * GET endpoint for categories
     *
     * @return List of existing categories
     */
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

    /**
     * GET endpoint for single category
     *
     * @param id Long id value of retrieved category
     * @return JSON response containing requested category
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> retrieveCategory(@PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(foundCategory);

        Link rootLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel(ROOT_LINK);

        categoryDTO.add(rootLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects that match with given category
     *
     * @param id Long id value of category
     * @return List of projects that match with given category
     */
    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> retrieveProjects(@PathVariable Long id) {

        Category foundCategory = categoryService.findCategory(id).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        List<ProjectDTO> projectDTOs = foundCategory.getProjectsCategories().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
