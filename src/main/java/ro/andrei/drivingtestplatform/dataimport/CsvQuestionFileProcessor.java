package ro.andrei.drivingtestplatform.dataimport;

import org.springframework.stereotype.Component;
import ro.andrei.drivingtestplatform.request.QuestionRequest;
import ro.andrei.drivingtestplatform.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvQuestionFileProcessor implements QuestionFileProcessor {
    private static final String HEADER = "question_title,answer1,isCorrect1,answer2,isCorrect2,answer3,isCorrect3,imageBase64";

    @Override
    public List<QuestionRequest> processFile(InputStream inputStream, Long examConfigId) throws IOException {
        List<QuestionRequest> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isHeaderSkipped = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (!isHeaderSkipped && line.equals(HEADER)) {
                    isHeaderSkipped = true;
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    QuestionRequest question = parseLine(line, examConfigId);
                    questions.add(question);
                } catch (IllegalArgumentException e) {
                    Logger.getInstance().error("Invalid line format: " + line + ". Skipping line. Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Logger.getInstance().error("Error while reading CSV file: " + e.getMessage());
            throw e;
        }

        return questions;
    }

    private QuestionRequest parseLine(String line, Long examConfigId) {
        String[] values = line.split(",");

        // Line length can be 7 or 8
        // if 8, it has image base64

        if (values.length < 7 || values.length > 8) {
            throw new IllegalArgumentException("Invalid line format. Expected 7 or 8 values, got " + values.length);
        }

        String questionTitle = values[0];
        List<String> answers = List.of(values[1], values[3], values[5]);
        List<Boolean> correctAnswers;

        try {
            correctAnswers = List.of(
                    Boolean.parseBoolean(values[2]),
                    Boolean.parseBoolean(values[4]),
                    Boolean.parseBoolean(values[6])
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing boolean values for correct answers.");
        }
        String imageBase64;
        if (values.length == 8) {
            imageBase64 = values[7];
        } else {
            imageBase64 = null;
        }
        return new QuestionRequest(null, questionTitle, examConfigId, answers, correctAnswers, imageBase64);
    }
}
