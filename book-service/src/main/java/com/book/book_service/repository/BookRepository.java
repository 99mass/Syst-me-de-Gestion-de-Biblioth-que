package com.book.book_service.repository;

import com.book.book_service.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitreContainingIgnoreCase(String titre);
    
    List<Book> findByAuteurContainingIgnoreCase(String auteur);
    
    List<Book> findByGenreContainingIgnoreCase(String genre);
    
    @Query("SELECT b FROM Book b WHERE (:titre IS NULL OR LOWER(b.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) " +
           "AND (:auteur IS NULL OR LOWER(b.auteur) LIKE LOWER(CONCAT('%', :auteur, '%'))) " +
           "AND (:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%')))")
    List<Book> searchBooks(@Param("titre") String titre, 
                          @Param("auteur") String auteur, 
                          @Param("genre") String genre);
}