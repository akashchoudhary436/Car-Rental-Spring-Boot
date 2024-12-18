package com.example.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // new employee
    @PostMapping("/register")
    public Employee registerEmployee(@RequestBody Employee employee) {
        return employeeService.registerEmployee(employee);
    }

    // Reseting employee password after first login
    @PutMapping("/reset-password/{employeeId}")
    public String resetPassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        employeeService.resetPassword(employeeId, newPassword);
        return "Password reset successfully";
    }
}
