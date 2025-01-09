package ro.andrei.drivingtestplatform.service;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
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

    public List<QuestionResponse> getQuestions() {
        return questionRepository.findAllByOrderByIdDesc()
                .stream()
                .map(q -> new QuestionResponse(q))
                .collect(Collectors.toList());
    }
    public void saveQuestion(QuestionRequest questionRequest) {
        boolean isAnswerArraysEqual = questionRequest.getCorrectAnswers().size() == questionRequest.getAnswers().size();
        boolean isCorrectAnswersArrayEmpty = questionRequest.getCorrectAnswers().size() == 0;
        boolean isUpdate = questionRequest.getId() != null;

        if(isCorrectAnswersArrayEmpty) {
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
            question = new Question(null, questionRequest.getText(), examConfiguration.getLicenseType(), new ArrayList<Answer>());
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

    public QuestionResponse getQuestion(Long id) {
        Question question = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        ExamConfiguration examConfiguration = examConfigurationRepository.findByLicenseType(question.getDrivingLicenseType());

        return new QuestionResponse(question.getId(), question.getQuestionText(), question.getDrivingLicenseType().toString(),
                examConfiguration.getId(),
                question.getAnswers().stream().map(Answer::getAnswerText).collect(Collectors.toList()),
                question.getAnswers().stream().map(Answer::isCorrect).collect(Collectors.toList())
        );
    }

}
