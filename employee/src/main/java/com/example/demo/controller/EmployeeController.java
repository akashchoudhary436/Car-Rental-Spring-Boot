package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Employee;
import com.example.demo.services.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Register a new employee
    @PostMapping("/register")
    public Employee registerEmployee(@Valid @RequestBody Employee employee) {
        try {
            return employeeService.registerEmployee(employee);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex.getMessage()); // Return meaningful error
        }
    }

    // Reset employee password after first login
    @PutMapping("/reset-password/{employeeId}")
    public String resetPassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        employeeService.resetPassword(employeeId, newPassword);
        return "Password reset successfully";
    }
}
