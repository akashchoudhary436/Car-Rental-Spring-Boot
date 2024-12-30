    package com.example.demo.controller;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    import com.example.demo.model.Employee;
    import com.example.demo.services.EmployeeService;
    
    import jakarta.servlet.http.HttpServletRequest;
    @RestController
    @RequestMapping("/api/employees")
    public class EmployeeController {
        
        @Autowired
        private EmployeeService employeeService;

        @PostMapping("/register")
        public ResponseEntity<String> registerEmployee(@RequestBody Employee employee) {
            if (employeeService.isContactNumberExist(employee.getContactNumber())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contact number already exists.");
            }
            if (employeeService.isEmailExist(employee.getEmailId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email ID already exists.");
            }
            if ("Temporary".equals(employee.getAccountType()) && employee.getExpiryDate() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expiry date is required for Temporary accounts.");
            }

            employeeService.registerEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");
        }

        @PostMapping("/login")
        public ResponseEntity<?> loginEmployee(@RequestParam Long userId, @RequestParam String password) {
            Employee employee = employeeService.loginEmployeeById(userId, password);
            if (employee != null) {
                return ResponseEntity.ok(employee);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid User ID or password.");
            }
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


  @GetMapping("/logout")
    public ResponseEntity<String> logoutEmployee(HttpServletRequest request) {
        try {
            // Invalidate the session to log out the user
            request.getSession().invalidate();
            return ResponseEntity.ok("Successfully logged out.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage());
        }
    }
        }