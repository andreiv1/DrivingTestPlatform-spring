package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "exam_attempt_questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_attempt_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ExamAttempt examAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "order_index")
    private int orderIndex;
}