package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.andrei.drivingtestplatform.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    boolean existsByIdAndQuestion_Id(Long answerId, Long questionId);
}
