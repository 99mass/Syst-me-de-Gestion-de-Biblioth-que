package com.user.user_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime dateInscription;
}