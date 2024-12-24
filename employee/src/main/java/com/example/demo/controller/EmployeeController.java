package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Employee;
import com.example.demo.services.EmployeeService;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerEmployee(Employee employee, Model model) {
        // Check if the contact number already exists
        if (employeeService.isContactNumberExist(employee.getContactNumber())) {
            model.addAttribute("error", "Contact number already exists.");
            return "register";
        }

        // Check if the email ID already exists
        if (employeeService.isEmailExist(employee.getEmailId())) {
            model.addAttribute("error", "Email ID already exists.");
            return "register";
        }

        // Check if Expiry date is provided for Temporary account type
        if ("Temporary".equals(employee.getAccountType()) && employee.getExpiryDate() == null) {
            model.addAttribute("error", "Expiry date is required for Temporary accounts.");
            return "register";
        }

        employeeService.registerEmployee(employee);
        model.addAttribute("message", "Registration successful! Your default password has been generated.");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginEmployee(@RequestParam Long userId, 
                                @RequestParam String password, 
                                @RequestParam(required = false) String newPassword, 
                                Model model) {
        model.addAttribute("userId", userId); // Retain userId
        model.addAttribute("password", password); // Retain password
        
        Employee employee = employeeService.loginEmployeeById(userId, password);
        if (employee != null) {
            if (employee.isFirstLogin()) {
                // First Login Check
                if (newPassword != null && !newPassword.isEmpty()) {
                    employeeService.resetPassword(employee.getId(), newPassword);
                    model.addAttribute("message", "Password updated successfully. Please log in again.");
                    return "login"; // Redirect to login page after password update
                } else {
                    model.addAttribute("firstLogin", true);
                    model.addAttribute("error", "You must change your default password.");
                    return "login";
                }
            } else {
                // Normal Login
                model.addAttribute("message", "Welcome, " + employee.getFullName() + "!");
                return "welcome";
            }
        } else {
            model.addAttribute("error", "Invalid User ID or password. Please try again.");
            return "login";
        }
    }

    
    @GetMapping("/logout")
    public String logout() {
        // Implement logout functionality here
        return "redirect:/login";  // Redirect to login page after logout
    }

    
    @PutMapping("/reset-password/{employeeId}")
    public ResponseEntity<String> resetPassword(@PathVariable Long employeeId, @RequestParam String newPassword) {
        try {
            employeeService.resetPassword(employeeId, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

        @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password"; // Return the reset password page
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long userId, 
                                @RequestParam String oldPassword, 
                                @RequestParam String newPassword, 
                                Model model) {
        Employee employee = employeeService.loginEmployeeById(userId, oldPassword);
        if (employee != null) {
            employeeService.resetPassword(userId, newPassword);
            model.addAttribute("message", "Password reset successfully.");
            return "login"; // Redirect to login page after successful reset
        } else {
            model.addAttribute("error", "Invalid User ID or old password.");
            return "reset-password"; // Return to reset password page if the user ID or password is incorrect
        }
    }

}
