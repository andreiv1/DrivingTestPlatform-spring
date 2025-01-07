package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "exam_configurations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private DrivingLicenseType licenseType;

    @Column(name = "duration_in_minutes", nullable = false)
    private int durationInMinutes;

    @Column(name = "number_of_questions", nullable = false)
    private int numberOfQuestions;

    @Column(name = "passing_score", nullable = false)
    private int passingScore;
}