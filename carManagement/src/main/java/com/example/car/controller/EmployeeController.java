package com.example.car.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.car.model.Employee;
import com.example.car.service.IEmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    public ResponseEntity<Employee> registerEmployee(@RequestBody @Valid Employee employee) {
        Employee registeredEmployee = employeeService.registerEmployee(employee);
        return new ResponseEntity<>(registeredEmployee, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Employee> login(@RequestParam String emailId, @RequestParam String password) {
        Employee employee = employeeService.login(emailId, password);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam String emailId, @RequestParam String newPassword) {
        employeeService.updatePassword(emailId, newPassword);
        return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
    }

   // Get employee by employeeId
   @GetMapping("/{employeeId}")
   public ResponseEntity<Employee> getEmployeeById(@PathVariable Long employeeId) {
       Employee employee = employeeService.getEmployeeById(employeeId);
       return employee != null ? 
              new ResponseEntity<>(employee, HttpStatus.OK) : 
              new ResponseEntity<>(HttpStatus.NOT_FOUND);
   }

   // Get all employees
   @GetMapping("/employees")
   public ResponseEntity<List<Employee>> getAllEmployees() {
       List<Employee> employees = employeeService.getAllEmployees();
       return new ResponseEntity<>(employees, HttpStatus.OK);
   }

   // Update employee details by employeeId
   @PutMapping("/{employeeId}")
   public ResponseEntity<Employee> updateEmployee(@PathVariable Long employeeId, @RequestBody Employee employee) {
       Employee updatedEmployee = employeeService.updateEmployee(employeeId, employee);
       return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
   }

   @DeleteMapping("/delete/{employeeId}")
   public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId) {
       employeeService.deleteEmployee(employeeId);
       return new ResponseEntity<>("Employee deleted successfully.", HttpStatus.OK);
   }
   @PostMapping("/test-expiration")
public ResponseEntity<String> testExpiration() {
    employeeService.deactivateExpiredAccounts();
    return ResponseEntity.ok("Expiration check triggered.");
}

   
}