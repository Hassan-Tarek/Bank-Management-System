package com.bms.repository;

import com.bms.entity.User;
import com.bms.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<User, Long> {

    @Query("SELECT u FROM User AS u WHERE (:role IS NULL OR u.role = :role)")
    List<User> findByFilters(@Param("role") Role role);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
