package ro.andrei.drivingtestplatform.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.dataimport.QuestionFileProcessor;
import ro.andrei.drivingtestplatform.model.Answer;
import ro.andrei.drivingtestplatform.model.ExamConfiguration;
import ro.andrei.drivingtestplatform.model.Question;
import ro.andrei.drivingtestplatform.repository.AnswerRepository;
import ro.andrei.drivingtestplatform.repository.ExamConfigurationRepository;
import ro.andrei.drivingtestplatform.repository.QuestionRepository;
import ro.andrei.drivingtestplatform.request.QuestionRequest;
import ro.andrei.drivingtestplatform.response.QuestionResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final ExamConfigurationRepository examConfigurationRepository;

    private final QuestionFileProcessor questionFileProcessor;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
                           AnswerRepository answerRepository,
                           ExamConfigurationRepository examConfigurationRepository,
                           QuestionFileProcessor questionFileProcessor) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.examConfigurationRepository = examConfigurationRepository;
        this.questionFileProcessor = questionFileProcessor;
    }

    @Transactional
    public List<QuestionResponse> getQuestions() {
        return questionRepository.findAllByOrderByIdDesc()
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }
    public void saveQuestion(QuestionRequest questionRequest) {
        boolean isAnswerArraysEqual = questionRequest.getCorrectAnswers().size() == questionRequest.getAnswers().size();
        boolean existsAtLeastOneCorrect = questionRequest.getCorrectAnswers().stream().anyMatch(Boolean::booleanValue);
        boolean isUpdate = questionRequest.getId() != null;

        if(!existsAtLeastOneCorrect) {
            throw new RuntimeException("At least one correct answer must be provided");
        }
        if(!isAnswerArraysEqual) {
            throw new RuntimeException("Answers and correct answers arrays must have the same size");

        }

        ExamConfiguration examConfiguration = examConfigurationRepository
                .findById(questionRequest.getExamConfigId())
                .orElseThrow(() -> new RuntimeException("Exam configuration not found"));


        Question question;
        if(isUpdate) {
            question = questionRepository.findById(questionRequest.getId()).get();
            question.setQuestionText(questionRequest.getText());
            question.setDrivingLicenseType(examConfiguration.getLicenseType());
        } else {
            question = new Question(null, questionRequest.getText(), examConfiguration.getLicenseType(), new ArrayList<>());

            if(questionRequest.getImageBase64() != null) {
                byte[] imageBytes = Base64.getDecoder().decode(questionRequest.getImageBase64());
                question.setImage(imageBytes);
            }
        }

        for(int i = 0; i < questionRequest.getAnswers().size(); i++) {
            Answer answer;
            if (isUpdate) {
                answer = question.getAnswers().get(i);
                answer.setAnswerText(questionRequest.getAnswers().get(i));
            } else {
                answer = new Answer(null, questionRequest.getAnswers().get(i), false, question);
                question.getAnswers().add(answer);
            }

            if (questionRequest.getCorrectAnswers().get(i) != null) {
                answer.setCorrect(questionRequest.getCorrectAnswers().get(i));
            }
            answerRepository.save(answer);
        }

        questionRepository.save(question);
    }

    public void importQuestionsFromFile(InputStream inputStream, Long examConfigId) {
        try {
            List<QuestionRequest> questions = questionFileProcessor.processFile(inputStream, examConfigId);
            for(QuestionRequest questionRequest : questions){
                saveQuestion(questionRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public QuestionResponse getQuestion(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        ExamConfiguration examConfiguration =
                examConfigurationRepository.findByLicenseType(question.getDrivingLicenseType());
        var response = new QuestionResponse(question);
        response.setExamConfigId(examConfiguration.getId());
        return response;
    }

}
