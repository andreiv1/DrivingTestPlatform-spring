package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cnp;

    @Column(nullable = false)
    private LocalDate joinDate;

    @ManyToOne
    @JoinColumn(name = "exam_configuration_id", nullable = false)
    private ExamConfiguration examConfig;
}
