package com.example.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register a new employee and generate default password
    public Employee registerEmployee(Employee employee) {
        // Generating default password
        String defaultPassword = generateDefaultPassword(employee);
        // Encoding the default password
        employee.setPassword(passwordEncoder.encode(defaultPassword));

        return employeeRepository.save(employee);
    }

    // default password logic 
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
            employee.setPassword(passwordEncoder.encode(newPassword)); //new password
            employee.setFirstLogin(false);  //changing pass
            employeeRepository.save(employee);
        }
    }
}
