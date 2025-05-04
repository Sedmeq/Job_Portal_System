package org.example.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.EmployerRequest;
import org.example.job_portal.dto.response.EmployerResponse;
import org.example.job_portal.enums.Role;
import org.example.job_portal.exception.NotFoundException;
import org.example.job_portal.model.Employer;
import org.example.job_portal.model.User;
import org.example.job_portal.repository.EmployerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public EmployerResponse createEmployer(EmployerRequest request) {
        User currentUser = userService.getCurrentUser();

        if (employerRepository.findByUser(currentUser).isPresent()) {
            throw new RuntimeException("Employer profile already exists for this user");
        }

        if (currentUser.getRole() != Role.EMPLOYER) {
            throw new RuntimeException("Only users with EMPLOYER role can create employer profiles");
        }

        Employer employer = Employer.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(currentUser)
                .build();

        Employer savedEmployer = employerRepository.save(employer);
        EmployerResponse response = modelMapper.map(savedEmployer, EmployerResponse.class);
        response.setUsername(currentUser.getUsername());
        return response;
    }

    public EmployerResponse getEmployerById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employer not found with id: " + id));

        EmployerResponse response = modelMapper.map(employer, EmployerResponse.class);
        response.setUsername(employer.getUser().getUsername());
        return response;
    }

    public EmployerResponse getCurrentEmployer() {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        EmployerResponse response = modelMapper.map(employer, EmployerResponse.class);
        response.setUsername(currentUser.getUsername());
        return response;
    }

    public EmployerResponse updateEmployer(EmployerRequest request) {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        employer.setName(request.getName());
        employer.setDescription(request.getDescription());

        Employer updatedEmployer = employerRepository.save(employer);
        EmployerResponse response = modelMapper.map(updatedEmployer, EmployerResponse.class);
        response.setUsername(currentUser.getUsername());
        return response;
    }
}