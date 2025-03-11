package com.user.user_service.controller;

import com.user.user_service.dto.CreateLoanRequest;
import com.user.user_service.dto.LoanDto;
import com.user.user_service.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Prêts", description = "API de gestion des prêts de livres")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Operation(summary = "Récupérer tous les prêts", description = "Retourne tous les prêts enregistrés dans le système")
    @ApiResponse(responseCode = "200", description = "Liste des prêts récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(summary = "Récupérer un prêt par ID", description = "Retourne un prêt spécifique en fonction de son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prêt trouvé", content = @Content(schema = @Schema(implementation = LoanDto.class))),
        @ApiResponse(responseCode = "404", description = "Prêt non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(loanService.getLoanById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Créer un nouveau prêt", description = "Crée un nouveau prêt de livre pour un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Prêt créé avec succès", content = @Content(schema = @Schema(implementation = LoanDto.class))),
        @ApiResponse(responseCode = "404", description = "Utilisateur ou livre non trouvé"),
        @ApiResponse(responseCode = "409", description = "Livre déjà emprunté ou utilisateur avec trop d'emprunts"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PostMapping
    public ResponseEntity<?> createLoan(@RequestBody CreateLoanRequest request) {
        try {
            LoanDto loanDto = loanService.createLoan(request);
            return new ResponseEntity<>(loanDto, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Retourner un livre", description = "Marque un prêt comme retourné et met à jour la disponibilité du livre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livre retourné avec succès", content = @Content(schema = @Schema(implementation = LoanDto.class))),
        @ApiResponse(responseCode = "404", description = "Prêt non trouvé"),
        @ApiResponse(responseCode = "409", description = "Le prêt est déjà marqué comme retourné"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable String id) {
        try {
            LoanDto loanDto = loanService.returnLoan(id);
            return ResponseEntity.ok(loanDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}