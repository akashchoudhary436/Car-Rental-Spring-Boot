package com.example.demo.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmailId(String emailId);
    Optional<Employee> findByContactNumber(String contactNumber);  // Optional method for contact number
    boolean existsByEmailId(String emailId);  // Checks if email ID exists
    boolean existsByContactNumber(String contactNumber);  // Checks if contact number exists
}
