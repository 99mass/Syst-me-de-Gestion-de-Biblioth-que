package com.user.user_service.repository;

import com.user.user_service.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    // Trouver tous les emprunts d'un utilisateur
    List<Loan> findByUserId(String userId);
    
    // Trouver un emprunt actif pour un livre donné
    List<Loan> findByBookIdAndStatut(Long bookId, Loan.LoanStatus statut);
    
    // Trouver tous les emprunts en retard
    List<Loan> findByStatut(Loan.LoanStatus statut);
}