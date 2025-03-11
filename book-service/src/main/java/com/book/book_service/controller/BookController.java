package com.book.book_service.controller;

import com.book.book_service.dto.BookDto;
import com.book.book_service.dto.AvailabilityRequest;
import com.book.book_service.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/books")
@Tag(name = "Livres", description = "API de gestion des livres de la bibliothèque")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "Récupérer tous les livres", description = "Retourne la liste de tous les livres disponibles dans la bibliothèque")
    @ApiResponse(responseCode = "200", description = "Liste des livres récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(summary = "Récupérer un livre par ID", description = "Retourne les détails d'un livre spécifique en fonction de son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livre trouvé", content = @Content(schema = @Schema(implementation = BookDto.class))),
        @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(
            @Parameter(description = "ID du livre à récupérer", required = true, example = "42")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Créer un nouveau livre", description = "Ajoute un nouveau livre à la bibliothèque")
    @ApiResponse(responseCode = "201", description = "Livre créé avec succès", content = @Content(schema = @Schema(implementation = BookDto.class)))
    @PostMapping
    public ResponseEntity<BookDto> createBook(
            @Parameter(description = "Détails du livre à créer", required = true)
            @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un livre", description = "Met à jour les informations d'un livre existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livre mis à jour avec succès", content = @Content(schema = @Schema(implementation = BookDto.class))),
        @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @Parameter(description = "ID du livre à mettre à jour", required = true, example = "42")
            @PathVariable Long id, 
            @Parameter(description = "Détails mis à jour du livre", required = true)
            @RequestBody BookDto bookDto) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, bookDto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Supprimer un livre", description = "Supprime un livre du catalogue de la bibliothèque")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Livre supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID du livre à supprimer", required = true, example = "42")
            @PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Rechercher des livres", description = "Recherche des livres selon différents critères (titre, auteur, genre)")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche", content = @Content(schema = @Schema(implementation = BookDto.class)))
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(
            @Parameter(description = "Titre du livre (recherche partielle)", example = "Harry Potter")
            @RequestParam(required = false) String title,
            @Parameter(description = "Nom de l'auteur (recherche partielle)", example = "Rowling")
            @RequestParam(required = false) String author,
            @Parameter(description = "Genre du livre", example = "Fantasy")
            @RequestParam(required = false) String genre) {
        return ResponseEntity.ok(bookService.searchBooks(title, author, genre));
    }

    @Operation(summary = "Mettre à jour la disponibilité d'un livre", 
               description = "Change l'état de disponibilité d'un livre (disponible ou non disponible)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès", content = @Content(schema = @Schema(implementation = BookDto.class))),
        @ApiResponse(responseCode = "404", description = "Livre non trouvé")
    })
    @PutMapping("/{id}/availability")
    public ResponseEntity<BookDto> updateBookAvailability(
            @Parameter(description = "ID du livre à mettre à jour", required = true, example = "42")
            @PathVariable Long id, 
            @Parameter(description = "Nouvelle disponibilité du livre", required = true)
            @RequestBody AvailabilityRequest request) {
        try {
            return ResponseEntity.ok(bookService.updateBookAvailability(id, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}