package org.example.job_portal.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "vacancy")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String description;
    String startDate;
    String endDate;
    String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employer_id")
    Employer employer;

    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    List<Application> applications;
}