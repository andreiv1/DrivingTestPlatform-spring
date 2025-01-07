package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
