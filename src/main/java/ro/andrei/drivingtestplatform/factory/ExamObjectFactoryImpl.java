package ro.andrei.drivingtestplatform.factory;

import org.springframework.stereotype.Component;
import ro.andrei.drivingtestplatform.model.*;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.enums.ExamStatus;
import ro.andrei.drivingtestplatform.util.ExamAttemptQuestionIterator;

import java.time.LocalDateTime;
import java.util.Iterator;

@Component
public class ExamObjectFactoryImpl implements ExamObjectFactory {

    @Override
    public ExamAttempt createExamAttempt(Candidate candidate, ExamConfiguration examConfiguration) {
        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setStatus(ExamStatus.NOT_STARTED);
        examAttempt.setCandidate(candidate);
        examAttempt.setLicenseType(examConfiguration.getLicenseType());
        examAttempt.setStartTime(null);
        examAttempt.setEndTime(null);

        return examAttempt;
    }

    @Override
    public ExamAttemptQuestion createExamAttemptQuestion(ExamAttempt examAttempt, Question question, int orderIndex) {
        ExamAttemptQuestion examAttemptQuestion = new ExamAttemptQuestion();
        examAttemptQuestion.setExamAttempt(examAttempt);
        examAttemptQuestion.setQuestion(question);
        examAttemptQuestion.setOrderIndex(orderIndex);

        return examAttemptQuestion;
    }

    @Override
    public ExamAttemptAnswer createExamAttemptAnswer(ExamAttempt examAttempt, Question question, Answer answer) {
        ExamAttemptAnswer examAttemptAnswer = new ExamAttemptAnswer();
        examAttemptAnswer.setExamAttempt(examAttempt);
        examAttemptAnswer.setQuestion(question);
        examAttemptAnswer.setAnsweredAt(LocalDateTime.now());
        examAttemptAnswer.setAnswer(answer);

        return examAttemptAnswer;
    }

    @Override
    public Iterator<ExamAttemptQuestion> createExamAttemptQuestionIterator(ExamAttempt examAttempt) {
        return new ExamAttemptQuestionIterator(examAttempt);
    }
}
