package com.example.demo.services;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public void registerEmployee(Employee employee) {
        String defaultPassword = generateDefaultPassword(employee);
        employee.setPassword(defaultPassword);
        employeeRepository.save(employee);
    }

    public Employee loginEmployee(String emailId, String password) {
        return employeeRepository.findByEmailId(emailId)
                .filter(e -> e.getPassword().equals(password))
                .orElse(null);
    }

    public Employee loginEmployeeById(Long userId, String password) {
        return employeeRepository.findById(userId)
                .filter(e -> e.getPassword().equals(password))
                .orElse(null);
    }

    private String generateDefaultPassword(Employee employee) {
        String firstLetter = employee.getAccountType().substring(0, 1).toUpperCase();
        String dobFormatted = employee.getDob().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int nameLength = employee.getFullName().length();
        return firstLetter + dobFormatted + nameLength;
    }

    public void resetPassword(Long employeeId, String newPassword) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + employeeId + " not found"));

        employee.setPassword(newPassword);
        employee.setFirstLogin(false);
        employeeRepository.save(employee);
    }

    // Check if the contact number already exists in the database
    public boolean isContactNumberExist(String contactNumber) {
        return employeeRepository.existsByContactNumber(contactNumber);
    }

    // Check if the email ID already exists in the database
    public boolean isEmailExist(String emailId) {
        return employeeRepository.existsByEmailId(emailId);
    }
}
