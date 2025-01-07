package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    }
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Question currentQuestion;
    private int currentQuestionIndex;



}
