package com.example.demo.Services;

import com.example.demo.DummyObject.Category;
import com.example.demo.DummyObject.Project;
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

    public Optional<Category> searchCategory(Long id) {

        return categoryRepository.findById(id);
    }

    public List<Project> findProjectsWithCategoryName(String name) {

        return categoryRepository.findProjectFromCategory(name);
    }
}
