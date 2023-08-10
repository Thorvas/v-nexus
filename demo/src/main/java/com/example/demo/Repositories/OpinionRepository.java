package com.example.demo.Repositories;

import com.example.demo.Objects.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository responsible for connection with opinions in database
 *
 * @author Thorvas
 */
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
}
