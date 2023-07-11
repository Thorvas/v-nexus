package com.example.demo.Repositories;

import com.example.demo.DummyObject.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {
    Optional<ApiKey> findByKey(String apiKey);
}
