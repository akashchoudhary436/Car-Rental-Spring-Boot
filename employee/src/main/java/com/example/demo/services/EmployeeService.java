package com.example.demo.services;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Register a new employee and generate default password
    public Employee registerEmployee(Employee employee) {
        // Check if email already exists
        Optional<Employee> existingByEmail = employeeRepository.findByEmail(employee.getEmail());
        if (existingByEmail.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if contact number already exists
        Optional<Employee> existingByContact = employeeRepository.findByContactNumber(employee.getContactNumber());
        if (existingByContact.isPresent()) {
            throw new IllegalArgumentException("Contact number already exists");
        }

        // Generate default password
        String defaultPassword = generateDefaultPassword(employee);
        employee.setPassword(defaultPassword);  // No encoding since no Spring Security
        return employeeRepository.save(employee);
    }

    // Generate default password based on logic
    private String generateDefaultPassword(Employee employee) {
        String accountTypeFirstLetter = employee.getAccountType().substring(0, 1).toUpperCase();
        String dobFormatted = employee.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int nameLength = employee.getFullName().length();
        return accountTypeFirstLetter + dobFormatted + nameLength;
    }

    // Reset password for the employee after first login
    public void resetPassword(Long employeeId, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setPassword(newPassword); // No encoding since no Spring Security
            employee.setFirstLogin(false);
            employeeRepository.save(employee);
        }
    }
}
