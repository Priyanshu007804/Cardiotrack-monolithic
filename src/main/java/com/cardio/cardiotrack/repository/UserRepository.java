package com.cardio.cardiotrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cardio.cardiotrack.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}