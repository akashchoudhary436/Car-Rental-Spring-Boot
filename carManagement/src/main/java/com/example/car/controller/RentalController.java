package com.example.car.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.car.model.Employee;
import com.example.car.model.Rental;
import com.example.car.service.EmployeeService;
import com.example.car.service.RentalService;
@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final EmployeeService employeeService;

    public RentalController(RentalService rentalService, EmployeeService employeeService) {
        this.rentalService = rentalService;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental, @RequestParam Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return ResponseEntity.badRequest().body(null);
        }
        rental.setEmployee(employee);
        Rental createdRental = rentalService.addRental(rental);
        return ResponseEntity.ok(createdRental);
    }

    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> rentals = rentalService.getAllRentals();

        // Include Employee details in the response
        rentals.forEach(rental -> rental.getEmployee().setPassword(null));  // Optionally, avoid exposing password
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Include Employee details in the response
        rental.getEmployee().setPassword(null);  // Optionally, avoid exposing password
        return ResponseEntity.ok(rental);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Rental>> getRentalsByEmployeeId(@PathVariable Long employeeId) {
        List<Rental> rentals = rentalService.getRentalsByEmployeeId(employeeId);
        
        // Include Employee details in the response
        rentals.forEach(rental -> rental.getEmployee().setPassword(null));  // Optionally, avoid exposing password
        return ResponseEntity.ok(rentals);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
