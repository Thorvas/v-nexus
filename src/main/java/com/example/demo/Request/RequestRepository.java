package com.example.demo.Request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for connection with requests in database
 *
 * @author Thorvas
 */
@Repository
public interface RequestRepository extends JpaRepository<VolunteerRequest, Long> {
}
