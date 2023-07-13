package com.example.demo.Repositories;

import com.example.demo.DummyObject.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUsername(String username);
}
