package com.openclassrooms.ChatTop.Service;

import com.openclassrooms.ChatTop.Model.Rental;
import com.openclassrooms.ChatTop.Repository.RentalRepository;
import com.openclassrooms.ChatTop.Repository.UserRepository;
import com.openclassrooms.ChatTop.dto.request.RentalRequestDTO;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private UserRepository userRepository;


    public RentalsListResponseDTO getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        RentalsListResponseDTO response = new RentalsListResponseDTO();
        response.setRentals(rentalMapper.toDtoList(rentals));
        return response;
    }

    public RentalResponseDTO getRentalById(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Rental not found with id: " + id));
        return rentalMapper.toDto(rental);
    }

    public RentalResponseDTO updateRental(Integer id, RentalRequestDTO requestDTO) {
        Rental rental = rentalMapper.toEntity(requestDTO);
        rental.setId(id);
        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(updatedRental);
    }
}

