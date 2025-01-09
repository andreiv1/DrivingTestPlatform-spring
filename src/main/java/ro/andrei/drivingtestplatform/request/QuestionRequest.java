package ro.andrei.drivingtestplatform.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private Long id;
    private String text;
    private Long examConfigId;
    private List<String> answers;
    private List<Boolean> correctAnswers;
}
