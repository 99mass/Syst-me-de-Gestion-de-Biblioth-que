package com.user.user_service.service;

import com.user.user_service.dto.UserDto;
import com.user.user_service.model.User;
import com.user.user_service.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return convertToDto(user.get());
        } else {
            throw new NoSuchElementException("Utilisateur non trouvé avec l'id: " + id);
        }
    }

    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(String id, UserDto userDto) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Préserver la date d'inscription originale
            LocalDateTime originalDate = user.getDateInscription();

            // Mettre à jour les propriétés
            BeanUtils.copyProperties(userDto, user);
            user.setId(id); // Assurer que l'ID reste le même
            user.setDateInscription(originalDate); // Restaurer la date d'inscription originale

            User updatedUser = userRepository.save(user);
            return convertToDto(updatedUser);
        } else {
            throw new NoSuchElementException("Utilisateur non trouvé avec l'id: " + id);
        }
    }

    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Utilisateur non trouvé avec l'id: " + id);
        }
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }
}