package ro.andrei.drivingtestplatform.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.model.*;
import ro.andrei.drivingtestplatform.repository.*;
import ro.andrei.drivingtestplatform.response.ExamAttemptResponse;
import ro.andrei.drivingtestplatform.response.ExamConfigurationResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
public class ExamService {
    private final AnswerRepository answerRepository;
    private final ExamConfigurationRepository examConfigurationRepository;
    private final ExamAttemptRepository examAttemptRepository;

    private final ExamAttemptAnswerRepository examAttemptAnswerRepository;

    private final ExamAttemptQuestionRepository examAttemptQuestionsRepository;
    private final CandidateRepository candidateRepository;
    private final QuestionRepository questionRepository;

    @PostConstruct
    private void init(){
        List<ExamConfiguration> examConfigurations = List.of(
                new ExamConfiguration(null, DrivingLicenseType.A, 20, 20, 17),
                new ExamConfiguration(null, DrivingLicenseType.B, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.C, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.D, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.E, 15, 11, 9),
                new ExamConfiguration(null, DrivingLicenseType.R, 20, 15, 13)
        );
        //Check if the exam configurations are already in the database
        if(examConfigurationRepository.count() == 0) {
            examConfigurationRepository.saveAll(examConfigurations);
        }
    }
    @Autowired
    public ExamService(AnswerRepository answerRepository, ExamConfigurationRepository examConfigurationRepository, ExamAttemptRepository examAttemptRepository, ExamAttemptAnswerRepository examAttemptAnswerRepository, ExamAttemptQuestionRepository examAttemptQuestionsRepository, CandidateRepository candidateRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.examConfigurationRepository = examConfigurationRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examAttemptAnswerRepository = examAttemptAnswerRepository;
        this.examAttemptQuestionsRepository = examAttemptQuestionsRepository;
        this.candidateRepository = candidateRepository;
        this.questionRepository = questionRepository;
    }


    public List<ExamConfigurationResponse> getExamConfigurations() {
        return examConfigurationRepository.findAll()
                .stream().map(e -> new ExamConfigurationResponse(e.getId(),e.getLicenseType().toString())).toList();
    }

    public void generateExam(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);

        if(candidate == null) {
            throw new RuntimeException("Candidate not found");
        }
        ExamConfiguration examConfiguration = candidate.getExamConfiguration();

        //Can't generate an exam attempt if one is already in progress or not started
        if(examAttemptRepository.existsByCandidateAndStatus(candidate, ExamStatus.IN_PROGRESS)) {
            throw new RuntimeException("An exam attempt is already in progress");
        }
        if(examAttemptRepository.existsByCandidateAndStatus(candidate, ExamStatus.NOT_STARTED)) {
            throw new RuntimeException("Candidate already has an exam attempt that has not started");
        }



        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setStatus(ExamStatus.NOT_STARTED);
        examAttempt.setLicenseType(examConfiguration.getLicenseType());
        examAttempt.setCandidate(candidate);
        examAttempt.setStartTime(null);
        examAttempt.setEndTime(null);

        var questions = questionRepository.getRandQuestionsByLicenseType(
                examConfiguration.getLicenseType().toString(),
                examConfiguration.getNumberOfQuestions());

        Set<ExamAttemptQuestion> examAttemptQuestions = new HashSet<>();
        int index = 0;
        for(var question : questions){
            var examAttemptQuestion = new ExamAttemptQuestion();
            examAttemptQuestion.setExamAttempt(examAttempt);
            examAttemptQuestion.setQuestion(question);
            examAttemptQuestion.setOrderIndex(questions.indexOf(question));
            examAttemptQuestion.setOrderIndex(index++);
            examAttemptQuestions.add(examAttemptQuestion);
        }
        examAttempt.setExamAttemptQuestions(examAttemptQuestions);
        examAttemptRepository.save(examAttempt);

    }

    public ExamAttemptResponse startExam(String candidateCnp) {
        ExamAttempt examAttempt = examAttemptRepository.findByCandidate_Cnp(candidateCnp);
        if(examAttempt == null) {
            throw new RuntimeException("Exam attempt not found");
        }
        if(examAttempt.getStatus() == ExamStatus.IN_PROGRESS) {
            //TODO continue exam
//            throw new RuntimeException("Exam attempt already in progress");
        }
        ExamConfiguration examConfiguration = examConfigurationRepository.findByLicenseType(examAttempt.getLicenseType());
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setStatus(ExamStatus.IN_PROGRESS);
        examAttempt.setEndTime(examAttempt.getStartTime().plusMinutes(examConfiguration.getDurationInMinutes()));

        examAttemptRepository.save(examAttempt);

        ExamAttemptResponse response = new ExamAttemptResponse();
        response.setId(examAttempt.getId());
        response.setStart(examAttempt.getStartTime());
        response.setEnd(examAttempt.getEndTime());

        var currentQuestion = examAttemptQuestionsRepository.findByExamAttempt_IdAndOrderIndex(examAttempt.getId(), 0);
        ExamAttemptResponse.Question examAttemptQuestionResponse = new ExamAttemptResponse.Question();
        examAttemptQuestionResponse.setId(currentQuestion.getId());
        examAttemptQuestionResponse.setText(currentQuestion.getQuestion().getQuestionText());

        var answers = new LinkedHashMap<Long,String>();
        for(var answer : currentQuestion.getQuestion().getAnswers()){
            answers.put(answer.getId(), answer.getAnswerText());
        }
        examAttemptQuestionResponse.setAnswers(answers);

        response.setCurrentQuestion(examAttemptQuestionResponse);

        return response;
    }

    public void saveExamAttemptAnswer(Long examAttemptId, Long questionId, List<Long> selectedAnswersIds) {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId).orElse(null);
        for(var selectedAnswerId : selectedAnswersIds){
            Question question = questionRepository.findById(questionId).orElse(null);


            ExamAttemptAnswer examAttemptAnswer = new ExamAttemptAnswer();
            examAttemptAnswer.setExamAttempt(examAttempt);
            examAttemptAnswer.setQuestion(question);
            examAttemptAnswer.setAnsweredAt(LocalDateTime.now());
            Answer answer = answerRepository.findById(selectedAnswerId).orElse(null);

            examAttemptAnswer.setAnswer(answer);
            examAttemptAnswerRepository.save(examAttemptAnswer);
        }

    }

    public ExamAttemptResponse getNextQuestion(Long examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId).orElse(null);
        if(examAttempt == null) {
            throw new RuntimeException("Exam attempt not found");
        }

        var currentQuestion = examAttemptQuestionsRepository.findByExamAttempt_IdAndOrderIndex(examAttempt.getId(), 1);
        ExamAttemptResponse response = new ExamAttemptResponse();
        response.setId(examAttempt.getId());
        response.setStart(examAttempt.getStartTime());
        response.setEnd(examAttempt.getEndTime());

        ExamAttemptResponse.Question examAttemptQuestionResponse = new ExamAttemptResponse.Question();
        examAttemptQuestionResponse.setId(currentQuestion.getId());
        examAttemptQuestionResponse.setText(currentQuestion.getQuestion().getQuestionText());
        return  response;
    }
}
