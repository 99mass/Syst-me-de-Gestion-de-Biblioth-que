package com.user.user_service.dto;

import com.user.user_service.model.Loan.LoanStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanDto {
    private String id;
    private String userId;
    private Long bookId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private LoanStatus statut;
    
    // Informations supplémentaires pour l'affichage
    private String titreLivre;
    private String nomUtilisateur;
    private String prenomUtilisateur;
}