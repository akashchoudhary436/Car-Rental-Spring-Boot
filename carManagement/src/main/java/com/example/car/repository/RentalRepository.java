package com.example.car.repository;

import com.example.car.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByEmployee_EmployeeId(Long employeeId);
}
