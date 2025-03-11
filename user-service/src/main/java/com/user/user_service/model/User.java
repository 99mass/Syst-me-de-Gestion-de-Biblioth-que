package com.user.user_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime dateInscription = LocalDateTime.now();
}