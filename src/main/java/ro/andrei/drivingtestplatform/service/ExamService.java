package ro.andrei.drivingtestplatform.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.model.*;
import ro.andrei.drivingtestplatform.repository.CandidateRepository;
import ro.andrei.drivingtestplatform.repository.ExamAttemptRepository;
import ro.andrei.drivingtestplatform.repository.ExamConfigurationRepository;
import ro.andrei.drivingtestplatform.repository.QuestionRepository;
import ro.andrei.drivingtestplatform.response.ExamConfigurationResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExamService {
    private final ExamConfigurationRepository examConfigurationRepository;

    private final ExamAttemptRepository examAttemptRepository;
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
    public ExamService(ExamConfigurationRepository examConfigurationRepository, ExamAttemptRepository examAttemptRepository, CandidateRepository candidateRepository, QuestionRepository questionRepository) {
        this.examConfigurationRepository = examConfigurationRepository;
        this.examAttemptRepository = examAttemptRepository;
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

    public void startExam(Long examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId).orElse(null);
        if(examAttempt == null) {
            throw new RuntimeException("Exam attempt not found");
        }
        ExamConfiguration examConfiguration = examConfigurationRepository.findByLicenseType(examAttempt.getLicenseType());
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setStatus(ExamStatus.IN_PROGRESS);
        examAttempt.setEndTime(examAttempt.getStartTime().plusMinutes(examConfiguration.getDurationInMinutes()));

        examAttemptRepository.save(examAttempt);
    }

}
