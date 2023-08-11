package com.example.demo.Opinion;

import com.example.demo.Opinion.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository responsible for connection with opinions in database
 *
 * @author Thorvas
 */
public interface OpinionRepository extends JpaRepository<Opinion, Long> {
}
