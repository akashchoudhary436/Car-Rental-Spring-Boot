package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Employee;

@Controller
public class EmployeeController {

    // Backend URL (Make sure this points to the correct backend port, 8080 in this case)
    @Value("${backend.url}")
    private String backendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerEmployee(Employee employee, Model model) {
        String url = backendUrl + "/api/employees/register";
        try {
            restTemplate.postForObject(url, employee, String.class);
            model.addAttribute("message", "Registration successful! Please log in.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginEmployee(@RequestParam Long userId, @RequestParam String password, Model model) {
        String url = backendUrl + "/api/employees/login?userId=" + userId + "&password=" + password;
        try {
            Employee employee = restTemplate.postForObject(url, null, Employee.class);
            model.addAttribute("message", "Welcome, " + employee.getFullName());
            return "welcome";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid login credentials");
            return "login";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long userId, @RequestParam String oldPassword, @RequestParam String newPassword, Model model) {
        String url = backendUrl + "/api/employees/reset-password/" + userId + "?newPassword=" + newPassword;
        try {
            restTemplate.put(url, null);
            model.addAttribute("message", "Password reset successfully.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to reset password");
            return "reset-password";
        }
    }

    // Logout endpoint
    @GetMapping("/logout")
    public String logoutEmployee() {
        String url = backendUrl + "/api/employees/logout";
        try {
            restTemplate.getForObject(url, String.class); // Call the backend logout endpoint
            return "redirect:/login"; // Redirect user to the login page after logout
        } catch (Exception e) {
            // Handle any potential errors
            return "error"; // Show an error page if logout fails
        }
    }
}
