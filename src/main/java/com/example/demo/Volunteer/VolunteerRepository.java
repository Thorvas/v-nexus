package com.example.demo.Volunteer;

import com.example.demo.Volunteer.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository responsible for connection with volunteers in database
 *
 * @author Thorvas
 */
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}
