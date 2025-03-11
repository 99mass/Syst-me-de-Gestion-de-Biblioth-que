package com.book.book_service.service;

import com.book.book_service.dto.BookDto;
import com.book.book_service.dto.AvailabilityRequest;
import com.book.book_service.model.Book;
import com.book.book_service.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public BookDto getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return convertToDto(book.get());
        } else {
            throw new NoSuchElementException("Livre non trouvé avec l'id: " + id);
        }
    }
    
    public BookDto createBook(BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return convertToDto(savedBook);
    }
    
    public BookDto updateBook(Long id, BookDto bookDto) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            BeanUtils.copyProperties(bookDto, book);
            book.setId(id); // Assurer que l'ID reste le même
            Book updatedBook = bookRepository.save(book);
            return convertToDto(updatedBook);
        } else {
            throw new NoSuchElementException("Livre non trouvé avec l'id: " + id);
        }
    }
    
    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Livre non trouvé avec l'id: " + id);
        }
    }
    
    public List<BookDto> searchBooks(String titre, String auteur, String genre) {
        return bookRepository.searchBooks(titre, auteur, genre).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public BookDto updateBookAvailability(Long id, AvailabilityRequest request) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isPresent()) {
            Book book = existingBook.get();
            book.setDisponible(request.isDisponible());
            Book updatedBook = bookRepository.save(book);
            return convertToDto(updatedBook);
        } else {
            throw new NoSuchElementException("Livre non trouvé avec l'id: " + id);
        }
    }
    
    private BookDto convertToDto(Book book) {
        BookDto bookDto = new BookDto();
        BeanUtils.copyProperties(book, bookDto);
        return bookDto;
    }
    
    private Book convertToEntity(BookDto bookDto) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book);
        return book;
    }
}