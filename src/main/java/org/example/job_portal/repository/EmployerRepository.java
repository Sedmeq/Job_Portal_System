package org.example.job_portal.repository;

import org.example.job_portal.model.Employer;
import org.example.job_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByUser(User user);
}
