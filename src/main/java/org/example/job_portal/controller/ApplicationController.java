package org.example.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.ApplicationRequest;
import org.example.job_portal.dto.response.ApplicationResponse;
import org.example.job_portal.enums.ApplicationStatus;
import org.example.job_portal.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/jobs/{vacancyId}/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApplicationResponse> applyForVacancy(
            @PathVariable Long vacancyId,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyForVacancy(vacancyId, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ApplicationResponse>> getUserApplications() {
        return ResponseEntity.ok(applicationService.getUserApplications());
    }

    @GetMapping("/jobs/{vacancyId}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getVacancyApplications(@PathVariable Long vacancyId) {
        return ResponseEntity.ok(applicationService.getVacancyApplications(vacancyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status));
    }
}
