package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.andrei.drivingtestplatform.model.Candidate;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.ExamStatus;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.candidate.cnp = :cnp " +
            "AND ea.status = 'NOT_STARTED' " +
            "ORDER BY ea.id DESC LIMIT 1")
    ExamAttempt findLatestNotStartedAttempt(@Param("cnp") String cnp);
    boolean existsByCandidateAndStatus(Candidate candidate, ExamStatus status);


}
