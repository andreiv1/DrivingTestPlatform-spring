package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.andrei.drivingtestplatform.model.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    boolean existsByIdAndQuestion_Id(Long answerId, Long questionId);
    List<Answer> findAllByQuestion_Id(Long questionId);
}
