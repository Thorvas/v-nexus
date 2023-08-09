package com.example.demo.Services;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.Objects.Category;
import com.example.demo.Objects.Project;
import com.example.demo.Repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Category> searchCategories() {

        return categoryRepository.findAll();
    }

    public void deleteCategory(Category category) {

        categoryRepository.delete(category);
    }

    public Optional<Category> findCategory(Long id) {

        return categoryRepository.findById(id);
    }

    public List<Project> findProjectsWithCategoryName(String name) {

        return categoryRepository.findProjectFromCategory(name);
    }

    public Category updateCategory(Category sourceCategory, CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        category.setId(sourceCategory.getId());
        categoryRepository.save(category);

        return category;
    }

    public Category createCategory(String name, String description, Integer popularity) {

        Category category = Category.builder()
                .categoryName(name)
                .categoryDescription(description)
                .categoryPopularity(popularity)
                .build();

        categoryRepository.save(category);

        return category;
    }
}
