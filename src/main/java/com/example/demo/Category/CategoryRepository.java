package com.example.demo.Category;

import com.example.demo.Category.Category;
import com.example.demo.Project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository responsible for connection with categories in database
 *
 * @author Thorvas
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT p FROM Project p JOIN p.categories c WHERE c.categoryName = :content")
    List<Project> findProjectFromCategory(String content);
}
