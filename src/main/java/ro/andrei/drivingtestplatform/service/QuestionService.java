package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.dataimport.QuestionFileProcessor;
import ro.andrei.drivingtestplatform.model.Answer;
import ro.andrei.drivingtestplatform.model.ExamAttemptQuestion;
import ro.andrei.drivingtestplatform.model.ExamConfiguration;
import ro.andrei.drivingtestplatform.model.Question;
import ro.andrei.drivingtestplatform.repository.AnswerRepository;
import ro.andrei.drivingtestplatform.repository.ExamConfigurationRepository;
import ro.andrei.drivingtestplatform.repository.QuestionRepository;
import ro.andrei.drivingtestplatform.request.QuestionRequest;
import ro.andrei.drivingtestplatform.response.QuestionResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final ExamConfigurationRepository examConfigurationRepository;

    private final QuestionFileProcessor questionFileProcessor;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository, ExamConfigurationRepository examConfigurationRepository, QuestionFileProcessor questionFileProcessor) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.examConfigurationRepository = examConfigurationRepository;
        this.questionFileProcessor = questionFileProcessor;
    }

    public List<QuestionResponse> getQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(q -> new QuestionResponse(q.getId(),q.getQuestionText(),q.getDrivingLicenseType().toString(),
                        null, null, null)).collect(Collectors.toList());
    }
    public void addQuestion(QuestionRequest questionRequest) {
        ExamConfiguration examConfiguration = examConfigurationRepository
                .findById(questionRequest.getExamConfigId()).orElseThrow(() -> new RuntimeException("Exam configuration not found"));
        var answers = new ArrayList<Answer>();
        Question question = new Question(null, questionRequest.getText(), examConfiguration.getLicenseType(), answers);
        for(int i = 0; i < questionRequest.getAnswers().size(); i++) {
            Answer a = new Answer(null, questionRequest.getAnswers().get(i), false, question);

            if(questionRequest.getCorrectAnswers().get(i) != null){
                a.setCorrect(questionRequest.getCorrectAnswers().get(i));
            }

            answers.add(a);
        }

        questionRepository.save(question);
    }

    public void importQuestionsFromCsv(InputStream inputStream, Long examConfigId) {
        try {
            var questions = questionFileProcessor.processFile(inputStream, examConfigId);
            for(QuestionRequest questionRequest : questions){
                addQuestion(questionRequest);
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
