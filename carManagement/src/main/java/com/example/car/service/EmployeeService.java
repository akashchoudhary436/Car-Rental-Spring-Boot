package com.example.car.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.car.exception.DuplicateContactNumberException;
import com.example.car.exception.DuplicateEmailException;
import com.example.car.exception.InvalidEntityException;
import com.example.car.exception.InvalidLoginException;
import com.example.car.model.Employee;
import com.example.car.repository.EmployeeRepository;

@Service
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee registerEmployee(Employee employee) {
        if (emailExists(employee.getEmailId())) {
            throw new DuplicateEmailException("Email already exists.");
        }
        if (contactNumberExists(employee.getContactNumber())) {
            throw new DuplicateContactNumberException("Contact number already exists.");
        }

        String defaultPassword = generateDefaultPassword(employee);
        employee.setPassword(defaultPassword);
        employee.setIsFirstLogin(true);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee login(String emailId, String password) {
        Employee employee = employeeRepository.findByEmailId(emailId);
        if (employee == null || !employee.getPassword().equals(password)) {
            throw new InvalidLoginException("Invalid email or password.");
        }

        // Check if employee is temporary and if their term has expired
        if ("temporary".equalsIgnoreCase(employee.getAccountType()) && employee.getExpiryDate().isBefore(LocalDate.now())) {
            throw new InvalidLoginException("Your term has expired. Please contact support.");
        }

        return employee;
    }

    @Override
    public void updatePassword(String emailId, String newPassword) {
        Employee employee = employeeRepository.findByEmailId(emailId);
        if (employee != null) {
            employee.setPassword(newPassword);
            employee.setIsFirstLogin(false);
            employeeRepository.save(employee);
        }
    }

    @Override
    public Employee updateEmployee(Long employeeId, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(employeeId).orElseThrow(() -> 
            new InvalidEntityException("Employee not found with ID: " + employeeId));

        existingEmployee.setFullName(employee.getFullName()); // Update full name
        existingEmployee.setEmailId(employee.getEmailId()); // Update emailId
        existingEmployee.setContactNumber(employee.getContactNumber()); // Update contact number
        existingEmployee.setDob(employee.getDob()); // Update date of birth
        existingEmployee.setAccountType(employee.getAccountType()); // Update account type

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> 
            new InvalidEntityException("Employee not found with ID: " + employeeId));

        employeeRepository.delete(employee);
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> 
            new InvalidEntityException("Employee not found with ID: " + employeeId));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    private String generateDefaultPassword(Employee employee) {
        String firstLetter = employee.getAccountType().substring(0, 1).toUpperCase();
        String dobFormatted = employee.getDob().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int nameLength = employee.getFullName().length();
        return firstLetter + dobFormatted + nameLength;
    }

    private boolean emailExists(String emailId) {
        return employeeRepository.findByEmailId(emailId) != null;
    }

    private boolean contactNumberExists(String contactNumber) {
        return employeeRepository.findByContactNumber(contactNumber) != null;
    }
}
