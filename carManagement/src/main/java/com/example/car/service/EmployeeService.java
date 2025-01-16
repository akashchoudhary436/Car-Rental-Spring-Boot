package com.example.car.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
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

    // Inject the EmailService to send registration emails
    private final EmailService emailService;

    public EmployeeService(EmployeeRepository employeeRepository, EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
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

        // Save the employee to the database
        Employee registeredEmployee = employeeRepository.save(employee);

        // Send an email to the employee after registration
        String subject = "Account Created Successfully";
        String body = "Dear " + employee.getFullName() + ",\n\n"
                + "Your account has been successfully created.\n"
                + "Your Employee ID: " + registeredEmployee.getEmployeeId() + "\n"
                + "Email: " + employee.getEmailId() + "\n"
                + "Your default password: " + defaultPassword + "\n\n"
                + "Please log in and change your password for security purposes.\n\n"
                + "Best Regards,\nTeam";

        // Send email notification
        emailService.sendEmail(employee.getEmailId(), subject, body);

        return registeredEmployee;
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
   // Send a notification email to the employee
   String subject = "Password Updated Successfully";
   String body = "Dear " + employee.getFullName() + ",\n\n"
           + "Your password has been successfully updated.\n"
           + "If you did not request this change, please contact support immediately.\n\n"
           + "Best Regards,\nTeam";

   emailService.sendEmail(employee.getEmailId(), subject, body);
} else {
   throw new InvalidEntityException("Employee not found with email: " + emailId);
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
      // Scheduler to check and deactivate expired temporary accounts
    @Override
    @Scheduled(cron = "*/5 * * * * ?") // Runs every 5 sec
        public void deactivateExpiredAccounts() {
        List<Employee> expiredEmployees = employeeRepository.findAll().stream()
            .filter(employee -> "temporary".equalsIgnoreCase(employee.getAccountType()) 
                    && employee.getExpiryDate().isBefore(LocalDate.now()))
            .toList();

        for (Employee employee : expiredEmployees) {
            // Send deactivation email to employee
            String subjectToEmployee = "Account Deactivation Notification";
            String bodyToEmployee = "Dear " + employee.getFullName() + ",\n\n"
                + "Your account has been deactivated as of " + employee.getExpiryDate() + ".\n"
                + "If you have any questions, please contact support.\n\n"
                + "Best Regards,\nTeam";

            emailService.sendEmail(employee.getEmailId(), subjectToEmployee, bodyToEmployee);

            // Send deactivation email to admin
            String subjectToAdmin = "Employee Account Deactivated";
            String bodyToAdmin = """
                                 The following temporary employee account has been deactivated:
                                 Employee ID: """ + employee.getEmployeeId() + "\n"
                + "Name: " + employee.getFullName() + "\n"
                + "Email: " + employee.getEmailId() + "\n"
                + "Contact Number: " + employee.getContactNumber() + "\n"
                + "Expiry Date: " + employee.getExpiryDate() + "\n\n"
                + "Best Regards,\nSystem";

            emailService.sendEmail("akashchoudhary436@gmail.com", subjectToAdmin, bodyToAdmin);

            // Remove or deactivate the employee
            employeeRepository.delete(employee); // Alternatively, you can update a status field
        }
    }
    
}

