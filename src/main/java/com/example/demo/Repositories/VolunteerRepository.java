package com.example.demo.Repositories;

import com.example.demo.Objects.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository responsible for connection with volunteers in database
 *
 * @author Thorvas
 */
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}
