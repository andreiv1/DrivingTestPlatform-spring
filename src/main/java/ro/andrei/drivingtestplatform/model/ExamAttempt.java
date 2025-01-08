package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "exam_attempts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExamStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private DrivingLicenseType licenseType;

    //Intrebarile generate pentru test
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "exam_attempt_questions",
//            joinColumns = @JoinColumn(name = "exam_attempt_id"),
//            inverseJoinColumns = @JoinColumn(name = "question_id")
//    )
//    private List<Question> questions;
    @OneToMany(mappedBy = "examAttempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ExamAttemptQuestion> examAttemptQuestions;

    //Raspunsurile la intrebarile generate pentru test date de utilizator
    @OneToMany(mappedBy = "examAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAttemptAnswer> examAttemptAnswers;

    //Candidatul care sustine incercarea
    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "current_question_index")
    private int currentQuestionIndex = 0;

    @Column(name = "wrong_answers")
    private int wrongAnswersCounter = 0;
}
