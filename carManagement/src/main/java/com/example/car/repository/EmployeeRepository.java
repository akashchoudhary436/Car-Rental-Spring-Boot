package com.example.car.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.car.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmailId(String emailId);
    Employee findByContactNumber(String contactNumber);
}
