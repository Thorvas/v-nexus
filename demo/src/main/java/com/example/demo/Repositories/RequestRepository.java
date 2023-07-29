package com.example.demo.Repositories;

import com.example.demo.Objects.VolunteerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<VolunteerRequest, Long> {
}
