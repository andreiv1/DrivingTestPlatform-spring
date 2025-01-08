package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cnp;

    @Column(name="join_date", nullable = false)
    private LocalDate joinDate;

    @ManyToOne
    @JoinColumn(name = "exam_configuration_id", nullable = false)
    private ExamConfiguration examConfiguration;
}
