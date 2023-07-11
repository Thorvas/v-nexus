package com.example.demo.Repositories;

import com.example.demo.DummyObject.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUsername(String username);
}
