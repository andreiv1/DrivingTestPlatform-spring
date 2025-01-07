package ro.andrei.drivingtestplatform.factory;

import ro.andrei.drivingtestplatform.model.*;

public interface ExamObjectFactory {
    ExamAttempt createExamAttempt(Candidate candidate, ExamConfiguration examConfiguration);
    ExamAttemptQuestion createExamAttemptQuestion(ExamAttempt examAttempt, Question question, int orderIndex);
    ExamAttemptAnswer createExamAttemptAnswer(ExamAttempt examAttempt, Question question, Answer answer);
}

