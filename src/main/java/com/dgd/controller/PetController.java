package com.dgd.controller;

import com.dgd.model.dto.pet.PetDto;
import com.dgd.model.entity.Pet;
import com.dgd.repository.PetRepository;
import com.dgd.repository.UserRepository;
import com.dgd.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pet")
public class PetController {
    private final PetService petService;

    @PostMapping("/register")
    public ResponseEntity<Pet> registerPet(@RequestBody @Valid PetDto petDto) {
        return ResponseEntity.ok(petService.registerPet(petDto));
    }
}
