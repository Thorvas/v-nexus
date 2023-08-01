package com.example.demo.Services;

import com.example.demo.Objects.Category;
import com.example.demo.Objects.Project;
import com.example.demo.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> searchCategories() {

        return categoryRepository.findAll();
    }

    public Optional<Category> findCategory(Long id) {

        return categoryRepository.findById(id);
    }

    public List<Project> findProjectsWithCategoryName(String name) {

        return categoryRepository.findProjectFromCategory(name);
    }

    public Category updateCategory(Category sourceCategory, Category targetCategory) {

        targetCategory.setId(sourceCategory.getId());
        categoryRepository.save(targetCategory);

        return targetCategory;
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
