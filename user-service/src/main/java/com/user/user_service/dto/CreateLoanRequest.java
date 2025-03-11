package com.user.user_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateLoanRequest {
    private String userId;
    private Long bookId;
    private LocalDate dateEmprunt = LocalDate.now();
    private LocalDate dateRetourPrevue;
}