package org.example.job_portal.service;

import lombok.RequiredArgsConstructor;
import org.example.job_portal.dto.request.VacancyRequest;
import org.example.job_portal.dto.response.CategoryResponse;
import org.example.job_portal.dto.response.VacancyResponse;
import org.example.job_portal.enums.Role;
import org.example.job_portal.exception.NotFoundException;
import org.example.job_portal.model.Category;
import org.example.job_portal.model.Employer;
import org.example.job_portal.model.User;
import org.example.job_portal.model.Vacancy;
import org.example.job_portal.repository.CategoryRepository;
import org.example.job_portal.repository.EmployerRepository;
import org.example.job_portal.repository.VacancyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final CategoryRepository categoryRepository;
    private final EmployerRepository employerRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public VacancyResponse createVacancy(VacancyRequest request) {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));

        Vacancy vacancy = Vacancy.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .email(request.getEmail())
                .category(category)
                .employer(employer)
                .build();

        Vacancy savedVacancy = vacancyRepository.save(vacancy);

        VacancyResponse response = modelMapper.map(savedVacancy, VacancyResponse.class);
        response.setCategory(modelMapper.map(category, CategoryResponse.class));
        response.setEmployerName(employer.getName());

        return response;
    }

    public List<VacancyResponse> getAllVacancies() {
        List<Vacancy> vacancies = vacancyRepository.findAll();
        return vacancies.stream()
                .map(vacancy -> {
                    VacancyResponse response = modelMapper.map(vacancy, VacancyResponse.class);
                    response.setCategory(modelMapper.map(vacancy.getCategory(), CategoryResponse.class));
                    response.setEmployerName(vacancy.getEmployer().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public VacancyResponse getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));

        VacancyResponse response = modelMapper.map(vacancy, VacancyResponse.class);
        response.setCategory(modelMapper.map(vacancy.getCategory(), CategoryResponse.class));
        response.setEmployerName(vacancy.getEmployer().getName());

        return response;
    }

    public List<VacancyResponse> getVacanciesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));

        List<Vacancy> vacancies = vacancyRepository.findByCategory(category);
        return vacancies.stream()
                .map(vacancy -> {
                    VacancyResponse response = modelMapper.map(vacancy, VacancyResponse.class);
                    response.setCategory(modelMapper.map(vacancy.getCategory(), CategoryResponse.class));
                    response.setEmployerName(vacancy.getEmployer().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<VacancyResponse> getVacanciesByEmployer() {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        List<Vacancy> vacancies = vacancyRepository.findByEmployer(employer);
        return vacancies.stream()
                .map(vacancy -> {
                    VacancyResponse response = modelMapper.map(vacancy, VacancyResponse.class);
                    response.setCategory(modelMapper.map(vacancy.getCategory(), CategoryResponse.class));
                    response.setEmployerName(employer.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public VacancyResponse updateVacancy(Long id, VacancyRequest request) {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));

        // Vakansiyanin isegoturene aid olub olmadigini yoxlayir
        if (!vacancy.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("You can only update your own vacancies");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));

        vacancy.setName(request.getName());
        vacancy.setDescription(request.getDescription());
        vacancy.setStartDate(request.getStartDate());
        vacancy.setEndDate(request.getEndDate());
        vacancy.setEmail(request.getEmail());
        vacancy.setCategory(category);

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);

        VacancyResponse response = modelMapper.map(updatedVacancy, VacancyResponse.class);
        response.setCategory(modelMapper.map(category, CategoryResponse.class));
        response.setEmployerName(employer.getName());

        return response;
    }

    public void deleteVacancy(Long id) {
        User currentUser = userService.getCurrentUser();
        Employer employer = employerRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Employer profile not found for current user"));

        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found with id: " + id));

        // Check if the vacancy belongs to the current employer or if the user is admin
        if (!vacancy.getEmployer().getId().equals(employer.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("You can only delete your own vacancies");
        }

        vacancyRepository.delete(vacancy);
    }
}