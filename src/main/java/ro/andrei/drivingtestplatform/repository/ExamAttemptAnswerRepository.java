package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.ExamAttemptAnswer;

public interface ExamAttemptAnswerRepository extends JpaRepository<ExamAttemptAnswer,Long> {
    boolean existsByQuestionIdAndExamAttemptId(Long questionId, Long examAttemptId);
}
