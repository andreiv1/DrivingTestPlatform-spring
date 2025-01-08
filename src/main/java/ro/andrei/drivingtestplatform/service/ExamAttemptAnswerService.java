package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.factory.ExamObjectFactory;
import ro.andrei.drivingtestplatform.model.Answer;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.ExamAttemptAnswer;
import ro.andrei.drivingtestplatform.model.Question;
import ro.andrei.drivingtestplatform.repository.AnswerRepository;
import ro.andrei.drivingtestplatform.repository.ExamAttemptAnswerRepository;
import ro.andrei.drivingtestplatform.repository.ExamAttemptRepository;
import ro.andrei.drivingtestplatform.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamAttemptAnswerService {
    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ExamObjectFactory examObjectFactory;
    private final ExamAttemptAnswerRepository examAttemptAnswerRepository;

    @Autowired
    public ExamAttemptAnswerService(ExamAttemptRepository examAttemptRepository,
                                    AnswerRepository answerRepository,
                                    QuestionRepository questionRepository,
                                    ExamObjectFactory examObjectFactory,
                                    ExamAttemptAnswerRepository examAttemptAnswerRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.examObjectFactory = examObjectFactory;
        this.examAttemptAnswerRepository = examAttemptAnswerRepository;
    }

    public void saveAnswers(Long examAttemptId, Long questionId, List<Long> selectedAnswersIds){
        ExamAttempt examAttempt = examAttemptRepository
                .findById(examAttemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt with id " + examAttemptId + " not found"));

        //Check if the answers belong to the question
        for(var selectedAnswerId : selectedAnswersIds){
            if(!answerRepository.existsByIdAndQuestion_Id(selectedAnswerId, questionId)){
                throw new RuntimeException("Answer with id " + selectedAnswerId + " does not belong to question with id " + questionId);
            }
        }

        List<ExamAttemptAnswer> givenAnswers = new ArrayList<>();

        for(var selectedAnswerId : selectedAnswersIds){
            Question question = questionRepository
                    .findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question with id " + questionId + " not found"));

            Answer answer = answerRepository.
                    findById(selectedAnswerId)
                    .orElseThrow(() -> new RuntimeException("Answer with id " + selectedAnswerId + " not found"));

            ExamAttemptAnswer examAttemptAnswer = examObjectFactory
                    .createExamAttemptAnswer(examAttempt, question, answer);

            givenAnswers.add(examAttemptAnswer);
        }
        examAttemptAnswerRepository.saveAll(givenAnswers);
        evaluateExamQuestion(questionId, givenAnswers);

        examAttempt.setCurrentQuestionIndex(examAttempt.getCurrentQuestionIndex() + 1);
        examAttemptRepository.save(examAttempt);

    }

    private void evaluateExamQuestion(Long questionId, List<ExamAttemptAnswer> givenAnswers) {
        //Get the correct answers for the question
        List<Answer> answers = answerRepository.findAllByQuestion_Id(questionId);

        List<Long> correctAnswersIds = answers.stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .sorted()
                .toList();

        List<Long> givenAnswersIds = givenAnswers.stream()
                .map(ExamAttemptAnswer::getAnswer)
                .map(Answer::getId)
                .sorted()
                .toList();

        boolean isNotCorrect = !correctAnswersIds.equals(givenAnswersIds);
        if(!correctAnswersIds.equals(givenAnswersIds)){
            //Increment the wrong answers count
            ExamAttempt examAttempt = givenAnswers.get(0).getExamAttempt();
            examAttempt.setWrongAnswersCounter(examAttempt.getWrongAnswersCounter() + 1);
            examAttemptRepository.save(examAttempt);
        }


    }
}
