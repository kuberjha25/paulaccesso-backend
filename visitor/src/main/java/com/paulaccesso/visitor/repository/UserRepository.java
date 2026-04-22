package com.paulaccesso.visitor.repository;

import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by empId (unique)
    Optional<User> findByEmpId(String empId);

    // Find by email (can return multiple)
    List<User> findByEmail(String email);

    // Find single user by email (first one)
    Optional<User> findFirstByEmail(String email);

    // Find login users (ADMIN or RECEPTIONIST) by email
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role IN ('ADMIN', 'RECEPTIONIST')")
    List<User> findLoginUsersByEmail(@Param("email") String email);

    // Find login user by empId (unique)
    @Query("SELECT u FROM User u WHERE u.empId = :empId AND u.role IN ('ADMIN', 'RECEPTIONIST')")
    Optional<User> findLoginUserByEmpId(@Param("empId") String empId);

    // Find employee by empId (unique)
    @Query("SELECT u FROM User u WHERE u.empId = :empId AND u.role = 'EMPLOYEE'")
    Optional<User> findEmployeeByEmpId(@Param("empId") String empId);

    // Check if any login user exists with this email
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.role IN ('ADMIN', 'RECEPTIONIST')")
    boolean existsLoginUserByEmail(@Param("email") String email);

    // Check if empId exists
    boolean existsByEmpId(String empId);

    // Get all employees with this email (multiple allowed)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role = 'EMPLOYEE'")
    List<User> findEmployeesByEmail(@Param("email") String email);

    // Get all users by role
    List<User> findByRole(Role role);

    // Check if user exists by email and role
    boolean existsByEmailAndRole(String email, Role role);
}