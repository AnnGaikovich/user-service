package org.example.userservice.repository;

import org.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Named method
    Optional<User> findByEmail(String email);

    // JPQL query
    @Query("SELECT u FROM User u WHERE u.active = true AND u.birthDate IS NOT NULL")
    List<User> findActiveUsersWithBirthDate();

    // Native SQL query
    //@Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "SELECT COUNT(*) FROM users WHERE email = :email AND id != :excludeId", nativeQuery = true)
    long countByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);

    // Native SQL for bulk update
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE users SET active = :active WHERE id = :id", nativeQuery = true)
    void updateActiveStatus(@Param("id") Long id, @Param("active") Boolean active);
}