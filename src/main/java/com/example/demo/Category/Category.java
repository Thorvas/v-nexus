package com.example.demo.Category;

import com.example.demo.Project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing category
 *
 * @author Thorvas
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "category")
public class Category {

    public Category() {

        this.projectsCategories = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_description")
    private String categoryDescription;

    @Column(name = "category_popularity")
    private Integer categoryPopularity;

    @ManyToMany(mappedBy = "categories")
    private List<Project> projectsCategories;

}
