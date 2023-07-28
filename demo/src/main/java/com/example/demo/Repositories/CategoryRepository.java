package com.example.demo.Repositories;

import com.example.demo.Objects.Category;
import com.example.demo.Objects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT p FROM Project p JOIN p.categories c WHERE c.categoryName = :content")
    List<Project> findProjectFromCategory(String content);
}
