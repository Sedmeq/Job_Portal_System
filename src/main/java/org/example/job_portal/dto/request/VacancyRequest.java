package org.example.job_portal.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyRequest {
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String email;
    private Long categoryId;
}