package org.example.job_portal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.job_portal.enums.ApplicationStatus;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "application")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    Vacancy vacancy;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    User candidate;

    String resume;

    @Column(name = "application_date")
    LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    ApplicationStatus status;
}