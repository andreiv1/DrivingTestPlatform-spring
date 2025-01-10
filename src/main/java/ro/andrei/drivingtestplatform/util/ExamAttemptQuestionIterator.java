package ro.andrei.drivingtestplatform.util;

import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.ExamAttemptQuestion;

import java.util.Iterator;

public class ExamAttemptQuestionIterator implements Iterator<ExamAttemptQuestion> {
    private final ExamAttempt examAttempt;

    public ExamAttemptQuestionIterator(ExamAttempt examAttempt) {
        this.examAttempt = examAttempt;
    }

    @Override
    public boolean hasNext() {
        return examAttempt.getCurrentQuestionIndex()
                < examAttempt.getExamAttemptQuestions().size();
    }

    @Override
    public ExamAttemptQuestion next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more questions");
        }
        ExamAttemptQuestion nextQuestion = examAttempt.getExamAttemptQuestions()
                .get(examAttempt.getCurrentQuestionIndex());
        examAttempt.setCurrentQuestionIndex(examAttempt.getCurrentQuestionIndex() + 1);
        return nextQuestion;
    }
}
