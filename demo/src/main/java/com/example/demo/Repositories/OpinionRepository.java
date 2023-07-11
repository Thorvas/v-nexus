package com.example.demo.Repositories;

import com.example.demo.DummyObject.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {
}
