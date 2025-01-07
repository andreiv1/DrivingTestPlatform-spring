package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.Candidate;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.ExamStatus;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    ExamAttempt findByCandidate_Cnp(String cnp);
    boolean existsByCandidateAndStatus(Candidate candidate, ExamStatus status);


}
