package com.book.book_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookDto {
    private Long id;
    private String titre;
    private String auteur;
    private String genre;
    private String isbn;
    private boolean disponible;
    private LocalDateTime dateAjout;
}