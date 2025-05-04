package org.example.job_portal.repository;

import org.example.job_portal.model.Category;
import org.example.job_portal.model.Employer;
import org.example.job_portal.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findByEmployer(Employer employer);
    List<Vacancy> findByCategory(Category category);
}