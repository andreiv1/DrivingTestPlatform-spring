package ro.andrei.drivingtestplatform.response;

import lombok.*;
import ro.andrei.drivingtestplatform.model.ExamAttemptQuestion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
@Getter
@Setter
//@Builder  //don't reinvent the wheel :)
public class ExamAttemptResponse
{
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Question {
        private Long id;
        private Long questionId;
        private String text;
        private LinkedHashMap<Long, String> answers;

        public Question(ExamAttemptQuestion q){
            this.id = q.getId();
            this.questionId = q.getQuestion().getId();
            this.text = q.getQuestion().getQuestionText();
            this.answers = new LinkedHashMap<>();
            q.getQuestion().getAnswers().forEach(a -> {
                this.answers.put(a.getId(), a.getAnswerText());
            });
        }

    }

    private final Long id;
    private final String start;
    private final String end;
    private final Question currentQuestion;
    private final int currentQuestionIndex;
    private final String status;
    private final int totalQuestions;
    private final int correctAnswers;
    private final int wrongAnswers;

    private ExamAttemptResponse(Builder builder) {
        this.id = builder.id;
        this.start = builder.start;
        this.end = builder.end;
        this.currentQuestion = builder.currentQuestion;
        this.currentQuestionIndex = builder.currentQuestionIndex;
        this.status = builder.status;
        this.totalQuestions = builder.totalQuestions;
        this.correctAnswers = builder.correctAnswers;
        this.wrongAnswers = builder.wrongAnswers;
    }
    public static class Builder {
        private Long id;
        private String start;
        private String end;
        private Question currentQuestion;
        private int currentQuestionIndex;
        private String status;
        private int totalQuestions;
        private int correctAnswers;
        private int wrongAnswers;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder start(LocalDateTime start) {
            this.start = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).toString();
            return this;
        }

        public Builder end(LocalDateTime end) {
            this.end = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).toString();
            return this;
        }

        public Builder currentQuestion(Question currentQuestion) {
            this.currentQuestion = currentQuestion;
            return this;
        }

        public Builder currentQuestionIndex(int currentQuestionIndex) {
            this.currentQuestionIndex = currentQuestionIndex;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public Builder correctAnswers(int correctAnswers) {
            this.correctAnswers = correctAnswers;
            return this;
        }

        public Builder wrongAnswers(int wrongAnswers) {
            this.wrongAnswers = wrongAnswers;
            return this;
        }

        public ExamAttemptResponse build() {
            return new ExamAttemptResponse(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
