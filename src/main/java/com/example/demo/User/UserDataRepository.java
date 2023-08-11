package com.example.demo.User;

import com.example.demo.User.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository responsible for connection with user related data in database
 *
 * @author Thorvas
 */
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByUsername(String username);
}
