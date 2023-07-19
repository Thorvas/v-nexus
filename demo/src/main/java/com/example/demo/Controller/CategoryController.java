package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DummyObject.Category;
import com.example.demo.DummyObject.Project;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<CategoryDTO>> retrieveCategories() {

        List<CategoryDTO> categoryDTOs = categoryService.searchCategories().stream()
                .map(categoryMapper::mapCategoryToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> retrieveCategory(@PathVariable Long id) {

        Category foundCategory = categoryService.searchCategory(id).orElseThrow(() -> new EntityNotFoundException("Requested entity could not be found."));

        CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(foundCategory);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjectDTO>> retrieveProjects(@RequestParam(name = "categoryName") String name) {

        List<Project> foundProjects = categoryService.findProjectsWithCategoryName(name);

        System.out.println(foundProjects);

        List<ProjectDTO> projectDTOs = foundProjects.stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
    }
}
