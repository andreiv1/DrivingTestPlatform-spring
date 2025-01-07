package ro.andrei.drivingtestplatform.visitor;

import ro.andrei.drivingtestplatform.model.Answer;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.ExamAttemptQuestion;

public interface ExamVisitor {
    void visit(ExamAttempt examAttempt);
    void visit(ExamAttemptQuestion examAttemptQuestion);
    void visit(Answer answer);
}