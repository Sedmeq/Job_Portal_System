package org.example.job_portal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.EmployerRequest;
import org.example.job_portal.dto.response.EmployerResponse;
import org.example.job_portal.service.EmployerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerResponse> createEmployer(@Valid @RequestBody EmployerRequest request) {
        return ResponseEntity.ok(employerService.createEmployer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployerResponse> getEmployerById(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getEmployerById(id));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerResponse> getCurrentEmployer() {
        return ResponseEntity.ok(employerService.getCurrentEmployer());
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerResponse> updateEmployer(@Valid @RequestBody EmployerRequest request) {
        return ResponseEntity.ok(employerService.updateEmployer(request));
    }
}