package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.ExamAttemptQuestion;

public interface ExamAttemptQuestionRepository extends JpaRepository<ExamAttemptQuestion, Long> {
    ExamAttemptQuestion findByExamAttempt_IdAndOrderIndex(Long examAttemptId, int orderIndex);
}