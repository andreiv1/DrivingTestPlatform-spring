package ro.andrei.drivingtestplatform.dataimport;

import org.springframework.stereotype.Component;
import ro.andrei.drivingtestplatform.request.QuestionRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvQuestionFileProcessor implements QuestionFileProcessor {
    @Override
    public List<QuestionRequest> processFile(InputStream inputStream, Long examConfigId) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        List<QuestionRequest> questions = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.equals("question_title,answer1,isCorrect1,answer2,isCorrect2,answer3,isCorrect3")) {
                continue;
            }
            String[] values = line.split(",");
            if (values.length != 7) {
                // Skip lines that don't have exactly 7 elements
                continue;
            }
            System.out.println(values[0]);
            String questionTitle = values[0];
            List<String> answers = new ArrayList<>(List.of(values[1], values[3], values[5]));
            List<Boolean> correctAnswers = new ArrayList<>(List.of(Boolean.parseBoolean(values[2]), Boolean.parseBoolean(values[4]), Boolean.parseBoolean(values[6])));
            QuestionRequest questionRequest = new QuestionRequest(questionTitle, examConfigId, answers, correctAnswers);
            questions.add(questionRequest);
        }

        br.close();
        return questions;

    }
}
