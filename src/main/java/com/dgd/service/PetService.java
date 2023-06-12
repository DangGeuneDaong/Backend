package com.dgd.service;

import com.dgd.exception.error.AuthenticationException;
import com.dgd.exception.type.ErrorCode;
import com.dgd.model.dto.pet.PetDto;
import com.dgd.model.entity.Pet;
import com.dgd.model.entity.User;
import com.dgd.repository.PetRepository;
import com.dgd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dgd.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public Pet registerPet(PetDto petDto) {
        User user = userRepository.findById(petDto.getUserId())
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND));

        Pet pet = Pet.builder()
                .userId(petDto.getUserId())
                .petType(petDto.getPetType())
                .petName(petDto.getPetName())
                .petGender(petDto.getPetGender())
                .petAge(petDto.getPetAge())
                .petSize(petDto.getPetSize())
                .build();

        user.addPet(pet);

        return petRepository.save(pet);
    }
}
