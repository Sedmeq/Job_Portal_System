package org.example.job_portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.job_portal.enums.ApplicationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long id;
    private VacancyResponse vacancy;
    private String candidateUsername;
    private String resume;
    private LocalDateTime applicationDate;
    private ApplicationStatus status;
}
