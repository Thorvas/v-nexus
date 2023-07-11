package com.example.demo.Repositories;

import com.example.demo.DummyObject.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
