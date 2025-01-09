package com.example.car.service;

import com.example.car.model.Rental;
import com.example.car.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental addRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    public List<Rental> getRentalsByEmployeeId(Long employeeId) {
        return rentalRepository.findByEmployee_EmployeeId(employeeId);
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}
