package org.example.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.VacancyRequest;
import org.example.job_portal.dto.response.VacancyResponse;
import org.example.job_portal.service.VacancyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping
    public ResponseEntity<List<VacancyResponse>> getAllVacancies() {
        return ResponseEntity.ok(vacancyService.getAllVacancies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponse> getVacancyById(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyService.getVacancyById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<VacancyResponse>> getVacanciesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(vacancyService.getVacanciesByCategory(categoryId));
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<VacancyResponse>> getEmployerVacancies() {
        return ResponseEntity.ok(vacancyService.getVacanciesByEmployer());
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<VacancyResponse> createVacancy(@Valid @RequestBody VacancyRequest request) {
        return ResponseEntity.ok(vacancyService.createVacancy(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<VacancyResponse> updateVacancy(
            @PathVariable Long id,
            @Valid @RequestBody VacancyRequest request) {
        return ResponseEntity.ok(vacancyService.updateVacancy(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }
}
