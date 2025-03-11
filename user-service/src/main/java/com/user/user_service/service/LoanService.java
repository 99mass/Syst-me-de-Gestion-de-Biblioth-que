package com.user.user_service.service;

import com.user.user_service.client.BookServiceClient;
import com.user.user_service.dto.*;
import com.user.user_service.model.Loan;
import com.user.user_service.model.User;
import com.user.user_service.repository.LoanRepository;
import com.user.user_service.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookServiceClient bookServiceClient;
    
    public List<LoanDto> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::enrichLoanDto)
                .collect(Collectors.toList());
    }
    
    public List<LoanDto> getLoansByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Utilisateur non trouvé avec l'id: " + userId);
        }
        
        return loanRepository.findByUserId(userId).stream()
                .map(this::enrichLoanDto)
                .collect(Collectors.toList());
    }
    
    public LoanDto getLoanById(String id) {
        Optional<Loan> loan = loanRepository.findById(id);
        if (loan.isPresent()) {
            return enrichLoanDto(loan.get());
        } else {
            throw new NoSuchElementException("Emprunt non trouvé avec l'id: " + id);
        }
    }
    
    public LoanDto createLoan(CreateLoanRequest request) {
        // Vérifier que l'utilisateur existe
        Optional<User> user = userRepository.findById(request.getUserId());
        if (!user.isPresent()) {
            throw new NoSuchElementException("Utilisateur non trouvé avec l'id: " + request.getUserId());
        }
        
        // Vérifier que le livre existe et est disponible
        try {
            BookDto book = bookServiceClient.getBookById(request.getBookId());
            if (!book.isDisponible()) {
                throw new IllegalStateException("Le livre n'est pas disponible pour l'emprunt: " + request.getBookId());
            }
            
            // Mettre à jour la disponibilité du livre
            bookServiceClient.updateBookAvailability(request.getBookId(), new AvailabilityRequest(false));
            
            // Créer l'emprunt
            Loan loan = new Loan();
            loan.setUserId(request.getUserId());
            loan.setBookId(request.getBookId());
            
            // Si une date d'emprunt est spécifiée, l'utiliser. Sinon, date du jour
            if (request.getDateEmprunt() != null) {
                loan.setDateEmprunt(request.getDateEmprunt());
            }
            
            // Date de retour prévue obligatoire
            if (request.getDateRetourPrevue() == null) {
                throw new IllegalArgumentException("La date de retour prévue est obligatoire");
            }
            loan.setDateRetourPrevue(request.getDateRetourPrevue());
            
            // Enregistrer l'emprunt
            Loan savedLoan = loanRepository.save(loan);
            return enrichLoanDto(savedLoan);
            
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                throw new NoSuchElementException("Livre non trouvé avec l'id: " + request.getBookId());
            }
            throw e;
        }
    }
    
    public LoanDto returnLoan(String id) {
        Optional<Loan> existingLoan = loanRepository.findById(id);
        if (!existingLoan.isPresent()) {
            throw new NoSuchElementException("Emprunt non trouvé avec l'id: " + id);
        }
        
        Loan loan = existingLoan.get();
        if (loan.getStatut() == Loan.LoanStatus.RETOURNE) {
            throw new IllegalStateException("Ce livre a déjà été retourné");
        }
        
        // Mettre à jour l'emprunt
        loan.setDateRetourEffective(LocalDate.now());
        loan.setStatut(Loan.LoanStatus.RETOURNE);
        
        // Mettre à jour la disponibilité du livre
        try {
            bookServiceClient.updateBookAvailability(loan.getBookId(), new AvailabilityRequest(true));
        } catch (Exception e) {
            // Même si la mise à jour échoue, on continue pour enregistrer le retour
            System.err.println("Erreur lors de la mise à jour de la disponibilité du livre: " + e.getMessage());
        }
        
        // Enregistrer les modifications
        Loan updatedLoan = loanRepository.save(loan);
        return enrichLoanDto(updatedLoan);
    }
    
    // Enrichir le DTO avec les informations du livre et de l'utilisateur
    private LoanDto enrichLoanDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        BeanUtils.copyProperties(loan, loanDto);
        
        try {
            // Récupérer les informations du livre
            BookDto book = bookServiceClient.getBookById(loan.getBookId());
            loanDto.setTitreLivre(book.getTitre());
            
            // Récupérer les informations de l'utilisateur
            Optional<User> user = userRepository.findById(loan.getUserId());
            if (user.isPresent()) {
                loanDto.setNomUtilisateur(user.get().getNom());
                loanDto.setPrenomUtilisateur(user.get().getPrenom());
            }
        } catch (Exception e) {
            // En cas d'erreur, on continue avec les informations de base
            System.err.println("Erreur lors de l'enrichissement des données d'emprunt: " + e.getMessage());
        }
        
        return loanDto;
    }
}