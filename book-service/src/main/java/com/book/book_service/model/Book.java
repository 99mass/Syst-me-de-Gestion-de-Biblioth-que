package com.book.book_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(nullable = false)
    private String auteur;
    
    private String genre;
    
    @Column(unique = true, nullable = false)
    private String isbn;
    
    private boolean disponible = true;
    
    private LocalDateTime dateAjout = LocalDateTime.now();
}