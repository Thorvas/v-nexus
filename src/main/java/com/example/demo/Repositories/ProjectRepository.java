package com.example.demo.Repositories;

import com.example.demo.Objects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository responsible for connection with projects in database
 *
 * @author Thorvas
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.projectDate = :date")
    List<Project> findWithDate(LocalDate date);

    @Query("SELECT p FROM Project p WHERE p.projectLocation = :location")
    List<Project> findWithLocation(String location);

    @Query("SELECT p FROM Project p WHERE p.projectStatus = :status")
    List<Project> findWithStatus(boolean status);
}
