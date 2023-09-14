package com.example.demo.Category;

import com.example.demo.Project.ProjectDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for categories
 * Categories can be modified and created only by administrators but retrieval is also allowed for regular authenticated users
 *
 * @author Thorvas
 */
@RestController
@Tag(name = "Categories")
@RequestMapping("/api/v1/categories")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel(ROOT_LINK);
    }

    /**
     * POST endpoint for categories. Allows administrators to create new categories for project matching
     *
     * @param category passed JSON category that will be saved within database
     * @return JSON response containing created category
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates new category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDTO> postCategory(@Valid @RequestBody CategoryDTO category) {

        CategoryDTO categoryDTO = categoryService.createCategory(category);

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .postCategory(category)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    /**
     * PUT endpoint for categories. Allows administrators to modify existing categories.
     *
     * @param category passed JSON category that will replace its existing counterpart
     * @param id       Long id value of updated category
     * @return JSON response containing updated category
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifies category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO category,
                                                      @PathVariable Long id) {


        CategoryDTO categoryDTO = categoryService.updateCategory(id, category);

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .updateCategory(category, id)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for categories. Allows administrators to delete certain categories
     *
     * @param id Long id value of deleted category
     * @return JSON response containing deleted category
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deletes category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {

        CategoryDTO categoryDTO = categoryService.deleteCategory(id);

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .deleteCategory(id)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for categories
     *
     * @return List of existing categories
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves list of categories", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CollectionModel<CategoryDTO>> retrieveCategories() {

        CollectionModel<CategoryDTO> categoryDTOs = categoryService.searchCategories();

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategories()).withRel(RESOURCE_PATH_LINK);

        categoryDTOs.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    /**
     * GET endpoint for single category
     *
     * @param id Long id value of retrieved category
     * @return JSON response containing requested category
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves certain category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDTO> retrieveCategory(@PathVariable Long id) {

        CategoryDTO categoryDTO = categoryService.searchCategory(id);

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveCategory(id)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects that match with given category
     *
     * @param id Long id value of category
     * @return List of projects that match with given category
     */
    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves list of project matching given category", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CollectionModel<ProjectDTO>> retrieveProjects(@PathVariable Long id) {

        List<ProjectDTO> projectDTOs = categoryService.retrieveProjectsFromCategory(id);

        Link selfLink = linkTo(methodOn(CategoryController.class)
                .retrieveProjects(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
