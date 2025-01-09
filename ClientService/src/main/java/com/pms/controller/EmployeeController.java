package com.pms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.model.Employee;

@Controller
@RequestMapping("/")
public class EmployeeController {

    @Autowired
    private RestTemplate restTemplate;

    private final String backendUrl = "http://localhost:8080/api/employees";

    // Helper method to extract error message
    private String extractErrorMessage(HttpClientErrorException e) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(e.getResponseBodyAsString());
            return root.path("message").asText(); // Extract "message" field from JSON
        } catch (Exception ex) {
            return "An unexpected error occurred.";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("employee", new Employee());
        return "register";
    }

    @PostMapping("/register")
    public String registerEmployee(@ModelAttribute Employee employee, Model model) {
        String url = backendUrl + "/register";
    
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Employee> request = new HttpEntity<>(employee, headers);
    
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            model.addAttribute("success", response.getBody());
            return "login";
    
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            
            // Assuming the backend is sending a structured response with field errors
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode root = objectMapper.readTree(e.getResponseBodyAsString());
                JsonNode fieldErrors = root.path("fieldErrors");
                model.addAttribute("fieldErrors", fieldErrors);
            } catch (Exception ex) {
                model.addAttribute("error", "An unexpected error occurred.");
            }
    
            model.addAttribute("error", errorMessage);
            return "register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String emailId, @RequestParam String password, Model model) {
        String url = backendUrl + "/login?emailId=" + emailId + "&password=" + password;

        try {
            Employee employee = restTemplate.postForObject(url, null, Employee.class);

            if (employee != null && employee.getIsFirstLogin()) {
                model.addAttribute("emailId", emailId);
                return "change-password";
            }

            model.addAttribute("fullName", employee.getFullName());
            return "welcome";

        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "login";
        }
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String emailId, @RequestParam String currentPassword,
                                 @RequestParam String newPassword, Model model) {
        String url = backendUrl + "/update-password?emailId=" + emailId + "&newPassword=" + newPassword;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Void> request = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            model.addAttribute("success", "Password updated successfully. Please login again.");
            return "login";

        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "change-password";
        }
    }

    // Get employee details by employeeId
    @GetMapping("/{employeeId}/details")
    public String getEmployeeDetails(@PathVariable Long employeeId, Model model) {
        String url = backendUrl + "/" + employeeId;

        try {
            Employee employee = restTemplate.getForObject(url, Employee.class);
            model.addAttribute("employee", employee);
            return "employee-details";
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "statuspage";  // Return status page in case of error
        }
    }

    // Get all employees
    @GetMapping("/all")
    public String getAllEmployees(Model model) {
        String url = backendUrl + "/employees";  // Backend URL to get all employees
        
        try {
            ResponseEntity<List<Employee>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Employee>>() {}
            );
            model.addAttribute("employees", response.getBody());  // Add employee list to model
            return "employee-list";  // Return employee list page
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            model.addAttribute("error", "Error fetching employees: " + e.getMessage());
        } catch (RestClientException e) {
            model.addAttribute("error", "Service unavailable. Please try again later.");
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred.");
        }
        
        return "error";  
    }

    // Delete employee by employeeId
    @GetMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable Long employeeId, Model model) {
        String url = backendUrl + "/delete/" + employeeId;  // Endpoint to delete employee
        try {
            restTemplate.delete(url);  // Call backend to delete employee
            model.addAttribute("success", "Employee deleted successfully.");
            return "redirect:/employees";  // Redirect to employee list after successful deletion
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "employee-list";  // Return to employee list page if error occurs
        }
    }


    // Show edit employee page by employeeId
    @GetMapping("/edit/{employeeId}")
    public String showEditEmployeePage(@PathVariable Long employeeId, Model model) {
        String url = backendUrl + "/" + employeeId; 
        try {
            Employee employee = restTemplate.getForObject(url, Employee.class);  
            model.addAttribute("employee", employee);
            return "edit-employee";  // Show employee edit page
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "employee-list";  // Return to employee list page if error occurs
        }
    }

    // Update employee details by employeeId
    @PostMapping("/edit/{employeeId}")
    public String editEmployee(@PathVariable Long employeeId, @ModelAttribute Employee employee, Model model) {
        String url = backendUrl + "/update/" + employeeId;  // Endpoint to update employee details
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Employee> request = new HttpEntity<>(employee, headers);
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);  // Call backend to update employee
            model.addAttribute("success", "Employee updated successfully.");
            return "redirect:/employees";  // Redirect to employee list after successful update
        } catch (HttpClientErrorException e) {
            String errorMessage = extractErrorMessage(e);
            model.addAttribute("error", errorMessage);
            return "edit-employee";  // Return to edit page if error occurs
        }
    }

}