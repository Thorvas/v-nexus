package com.example.demo.Repositories;

import com.example.demo.DummyObject.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
