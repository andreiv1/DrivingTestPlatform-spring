package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionResponse {
    private Long id;
    private String text;
    private String licenseType;
    private Long examConfigId;
    private List<String> answers;
    private List<Boolean> correctAnswers;
}
