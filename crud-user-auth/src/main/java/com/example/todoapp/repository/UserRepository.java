package com.example.todoapp.repository;

import com.example.todoapp.domain.User;
import com.example.todoapp.domain.enums.ERole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findTop10ByOrderByIdDesc();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_MODERATOR'")
    List<User> findAllModerators();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_ADMIN'")
    List<User> findAllAdmins();
    
    @Query("SELECT COUNT(u) FROM User u")
    Long countUsers();

    Page<User> findByRoles_Name(ERole role, Pageable page);
}
