package com.example.car.service;

import java.util.List;

import com.example.car.model.Employee;

public interface IEmployeeService {
    Employee registerEmployee(Employee employee);
    Employee login(String emailId, String password);
    void updatePassword(String emailId, String newPassword);
    Employee getEmployeeById(Long employeeId);  
    List<Employee> getAllEmployees();
    Employee updateEmployee(Long employeeId, Employee employee);  
    void deleteEmployee(Long employeeId);  
}
