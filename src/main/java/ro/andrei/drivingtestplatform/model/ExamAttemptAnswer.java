package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_attempt_answers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    private ExamAttempt examAttempt; // Legătura cu examenul

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // Întrebarea la care s-a răspuns

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer; // Răspunsul dat de candidat

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
}
