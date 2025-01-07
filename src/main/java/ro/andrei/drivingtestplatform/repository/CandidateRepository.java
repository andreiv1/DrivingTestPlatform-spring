package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
