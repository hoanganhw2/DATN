package com.learning.englishpro.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String courseTitle;
    private LocalDateTime purchaseDate;
    private String status;
}
