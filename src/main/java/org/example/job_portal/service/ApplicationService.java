package org.example.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.ApplicationRequest;
import org.example.job_portal.dto.response.ApplicationResponse;
import org.example.job_portal.enums.ApplicationStatus;
import org.example.job_portal.enums.Role;
import org.example.job_portal.exception.NotFoundException;
import org.example.job_portal.model.Application;
import org.example.job_portal.model.Employer;
import org.example.job_portal.model.User;
import org.example.job_portal.model.Vacancy;
import org.example.job_portal.repository.ApplicationRepository;
import org.example.job_portal.repository.EmployerRepository;
import org.example.job_portal.repository.VacancyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VacancyRepository vacancyRepository;
    private final EmployerRepository employerRepository;
    private final UserService userService;
    private final VacancyService vacancyService;
    private final ModelMapper modelMapper;


    public ApplicationResponse applyForVacancy(Long vacancyId, ApplicationRequest request) {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.EMPLOYER) {
            throw new RuntimeException("Employers cannot apply for jobs");
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + vacancyId));

        // bu vakansiyaya muraciet edib etmediyini yoxlayir
        List<Application> userApplications = applicationRepository.findByCandidate(currentUser);
        boolean alreadyApplied = userApplications.stream()
                .anyMatch(app -> app.getVacancy().getId().equals(vacancyId));

        if (alreadyApplied) {
            throw new RuntimeException("You have already applied for this vacancy");
        }

        Application application = Application.builder()
                .vacancy(vacancy)
                .candidate(currentUser)
                .resume(request.getResume())
                .applicationDate(LocalDateTime.now())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);

        ApplicationResponse response = modelMapper.map(savedApplication, ApplicationResponse.class);
        response.setVacancy(vacancyService.getVacancyById(vacancyId));
        response.setCandidateUsername(currentUser.getUsername());

        return response;
    }

    public List<ApplicationResponse> getUserApplications() {
        User currentUser = userService.getCurrentUser();
        List<Application> applications = applicationRepository.findByCandidate(currentUser);

        return applications.stream()
                .map(application -> {
                    ApplicationResponse response = modelMapper.map(application, ApplicationResponse.class);
                    response.setVacancy(vacancyService.getVacancyById(application.getVacancy().getId()));
                    response.setCandidateUsername(currentUser.getUsername());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getVacancyApplications(Long vacancyId) {
        User currentUser = userService.getCurrentUser();

        // Only allow employer who owns the vacancy or admin to see applications
        if (currentUser.getRole() != Role.ADMIN) {
            Employer employer = employerRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

            Vacancy vacancy = vacancyRepository.findById(vacancyId)
                    .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + vacancyId));

            if (!vacancy.getEmployer().getId().equals(employer.getId())) {
                throw new RuntimeException("You can only view applications for your own vacancies");
            }
        }

        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + vacancyId));

        return vacancy.getApplications().stream()
                .map(application -> {
                    ApplicationResponse response = modelMapper.map(application, ApplicationResponse.class);
                    response.setVacancy(vacancyService.getVacancyById(vacancyId));
                    response.setCandidateUsername(application.getCandidate().getUsername());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public ApplicationResponse getApplicationById(Long id) {
        User currentUser = userService.getCurrentUser();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + id));

        // Check if the user is the applicant, the employer of the vacancy, or an admin
        boolean isApplicant = application.getCandidate().getId().equals(currentUser.getId());
        boolean isEmployer = false;

        if (currentUser.getRole() == Role.EMPLOYER) {
            Employer employer = employerRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));
            isEmployer = application.getVacancy().getEmployer().getId().equals(employer.getId());
        }

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isApplicant && !isEmployer && !isAdmin) {
            throw new RuntimeException("You don't have permission to view this application");
        }

        ApplicationResponse response = modelMapper.map(application, ApplicationResponse.class);
        response.setVacancy(vacancyService.getVacancyById(application.getVacancy().getId()));
        response.setCandidateUsername(application.getCandidate().getUsername());

        return response;
    }

    public ApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status) {
        User currentUser = userService.getCurrentUser();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + id));

        // Only employer who owns the vacancy or admin can update status
        if (currentUser.getRole() != Role.ADMIN) {
            Employer employer = employerRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

            if (!application.getVacancy().getEmployer().getId().equals(employer.getId())) {
                throw new RuntimeException("You can only update status for applications to your own vacancies");
            }
        }

        // Validate that the provided status is one of the allowed values
        if (status != ApplicationStatus.PENDING &&
                status != ApplicationStatus.REVIEWED &&
                status != ApplicationStatus.REJECTED &&
                status != ApplicationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Invalid application status: " + status);
        }

        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);

        ApplicationResponse response = modelMapper.map(updatedApplication, ApplicationResponse.class);
        response.setVacancy(vacancyService.getVacancyById(updatedApplication.getVacancy().getId()));
        response.setCandidateUsername(updatedApplication.getCandidate().getUsername());

        return response;
    }
}
