package com.user.user_service.client;

import com.user.user_service.dto.BookDto;
import com.user.user_service.dto.AvailabilityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "BOOK-SERVICE")
public interface BookServiceClient {
    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
    
    @PutMapping("/api/books/{id}/availability")
    BookDto updateBookAvailability(@PathVariable("id") Long id, @RequestBody AvailabilityRequest request);
}