package org.example.job_portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyResponse {
    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String email;
    private CategoryResponse category;
    private String employerName;
}