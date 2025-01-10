package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.Answer;
import ro.andrei.drivingtestplatform.model.Question;
import ro.andrei.drivingtestplatform.util.ByteArrayToBase64;

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
    private String imageBase64 = "";

    public QuestionResponse(Question q) {
         this.id = q.getId();
         this.text = q.getQuestionText();
         this.licenseType = q.getDrivingLicenseType().toString();
         this.answers = q.getAnswers().stream().map(Answer::getAnswerText).toList();
         this.correctAnswers = q.getAnswers().stream().map(Answer::isCorrect).toList();
         this.imageBase64 = ByteArrayToBase64.convertByteArrayToBase64(q.getImage());
    }
}
