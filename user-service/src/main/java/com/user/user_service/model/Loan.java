package com.user.user_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "loans")
public class Loan {
    @Id
    private String id;
    
    private String userId;
    private Long bookId;
    
    private LocalDate dateEmprunt = LocalDate.now();
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    
    private LoanStatus statut = LoanStatus.EMPRUNTE;
    
    // Énumération pour les statuts d'emprunt
    public enum LoanStatus {
        EMPRUNTE,      // Livre emprunté, pas encore retourné
        RETOURNE,      // Livre retourné
        EN_RETARD      // Livre non retourné à la date prévue
    }
}