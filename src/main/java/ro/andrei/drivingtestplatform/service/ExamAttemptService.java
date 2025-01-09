package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.exceptions.ExamAttemptNotFoundException;
import ro.andrei.drivingtestplatform.factory.ExamObjectFactory;
import ro.andrei.drivingtestplatform.model.*;
import ro.andrei.drivingtestplatform.model.ExamAttempt;
import ro.andrei.drivingtestplatform.model.enums.ExamStatus;
import ro.andrei.drivingtestplatform.repository.*;
import ro.andrei.drivingtestplatform.response.ExamAttemptListingRespose;
import ro.andrei.drivingtestplatform.response.ExamAttemptResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExamAttemptService {
    private final ExamAttemptRepository examAttemptRepository;
    private final CandidateRepository candidateRepository;
    private final ExamObjectFactory examObjectFactory;
    private final QuestionRepository questionRepository;
    private final ExamConfigurationRepository examConfigurationRepository;
    private final ExamAttemptQuestionRepository examAttemptQuestionsRepository;
    private final ExamAttemptAnswerService examAttemptAnswerService;
    @Autowired
    public ExamAttemptService(ExamAttemptRepository examAttemptRepository,
                              CandidateRepository candidateRepository,
                              ExamObjectFactory examObjectFactory,
                              QuestionRepository questionRepository,
                              ExamConfigurationRepository examConfigurationRepository,
                              ExamAttemptQuestionRepository examAttemptQuestionsRepository,
                              ExamAttemptAnswerService examAttemptAnswerService) {
        this.examAttemptRepository = examAttemptRepository;
        this.candidateRepository = candidateRepository;
        this.examObjectFactory = examObjectFactory;
        this.questionRepository = questionRepository;
        this.examConfigurationRepository = examConfigurationRepository;
        this.examAttemptQuestionsRepository = examAttemptQuestionsRepository;
        this.examAttemptAnswerService = examAttemptAnswerService;
    }

    public List<ExamAttemptListingRespose> getExamAttemptsByCandidateId(Long candidateId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return examAttemptRepository.findAllByCandidate_IdOrderByIdDesc(candidateId)
                .stream()
                .map(ExamAttemptListingRespose::new)
                .collect(Collectors.toList());
    }

    /**
     * Generate an exam attempt for a candidate
     * @param candidateId the candidate id
     */
    public void generate(Long candidateId){
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        ExamConfiguration examConfiguration = candidate.getExamConfiguration();

        if(examAttemptRepository.existsByCandidateAndStatus(candidate, ExamStatus.IN_PROGRESS)) {
            throw new RuntimeException("An exam attempt is already in progress");
        }
        if(examAttemptRepository.existsByCandidateAndStatus(candidate, ExamStatus.NOT_STARTED)) {
            throw new RuntimeException("Candidate already has an exam attempt that has not started");
        }

        ExamAttempt examAttempt = examObjectFactory.createExamAttempt(candidate, examConfiguration);

        //Check if there are enough questions to generate the exam
        boolean enoughQuestions = questionRepository
                .countByDrivingLicenseType(examConfiguration.getLicenseType()) >= examConfiguration.getNumberOfQuestions();

        if(!enoughQuestions) {
            throw new RuntimeException("Not enough questions to generate the exam");
        }

        var questions = questionRepository.getRandQuestionsByLicenseType(
                examConfiguration.getLicenseType().toString(),
                examConfiguration.getNumberOfQuestions()
        );

        Set<ExamAttemptQuestion> examAttemptQuestions = new HashSet<>();
        int index = 0;
        for(var question : questions){
            ExamAttemptQuestion examAttemptQuestion = examObjectFactory
                    .createExamAttemptQuestion(examAttempt, question, index++);
            examAttemptQuestions.add(examAttemptQuestion);
        }
        examAttempt.setExamAttemptQuestions(examAttemptQuestions);
        examAttemptRepository.save(examAttempt);
    }

    /**
     * Start an exam attempt for a candidate
     * @return the exam attempt response
     */
    public ExamAttemptResponse start(String candidateCnp) throws ExamAttemptNotFoundException {
        ExamAttempt examAttempt = examAttemptRepository.findLatestNotStartedAttempt(candidateCnp);
        if(examAttempt == null) {
            throw new ExamAttemptNotFoundException("Exam attempt not found");
        }
        ExamConfiguration examConfiguration = examConfigurationRepository.findByLicenseType(examAttempt.getLicenseType());
        examAttempt.setStartTime(LocalDateTime.now());
        examAttempt.setStatus(ExamStatus.IN_PROGRESS);
        examAttempt.setEndTime(examAttempt.getStartTime().plusMinutes(examConfiguration.getDurationInMinutes()));
        examAttemptRepository.save(examAttempt);

        ExamAttemptQuestion currentQuestion = examAttemptQuestionsRepository
                .findByExamAttempt_IdAndOrderIndex(
                        examAttempt.getId(),
                        examAttempt.getCurrentQuestionIndex());

        ExamAttemptResponse.Question examAttemptQuestionResponse = new ExamAttemptResponse.Question(currentQuestion);

        return ExamAttemptResponse.builder()
                .id(examAttempt.getId())
                .start(examAttempt.getStartTime())
                .end(examAttempt.getEndTime())
                .totalQuestions(examConfiguration.getNumberOfQuestions())
                .correctAnswers(0)
                .wrongAnswers(0)
                .status(examAttempt.getStatus().toString())
                .currentQuestion(examAttemptQuestionResponse)
                .currentQuestionIndex(examAttempt.getCurrentQuestionIndex())
                .build();
    }

    /**
     * Save the answers for the current question and return the next question
     * @param examAttemptId the exam attempt id
     * @param questionId the question id
     * @param selectedAnswersIds the selected answers ids
     * @return the next question
     */
    public ExamAttemptResponse continueExam(Long examAttemptId, Long questionId, List<Long> selectedAnswersIds) throws ExamAttemptNotFoundException {
        examAttemptAnswerService.saveAnswers(examAttemptId, questionId, selectedAnswersIds);
        return nextQuestion(examAttemptId);
    }
    private ExamAttemptResponse nextQuestion(Long examAttemptId) throws ExamAttemptNotFoundException {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId).orElse(null);

        //Check if the exam attempt exists
        if(examAttempt == null) {
            throw new ExamAttemptNotFoundException("Exam attempt not found");
        }

        //Check if the exam is not failed (multiple wrong answers)
        ExamConfiguration examConfiguration = examConfigurationRepository.findByLicenseType(examAttempt.getLicenseType());

        int maxAllowedMistakes = examConfiguration.getNumberOfQuestions() - examConfiguration.getPassingScore();
        boolean isExamFailed = examAttempt.getWrongAnswersCounter() > maxAllowedMistakes;

        if(isExamFailed) {
            examAttempt.setStatus(ExamStatus.FAILED);
            examAttempt.setEndTime(LocalDateTime.now());
            examAttemptRepository.save(examAttempt);
            return null;
        }

        //Check if there are any questions left
        if(examAttempt.getCurrentQuestionIndex() >= examConfiguration.getNumberOfQuestions()) {
            //TODO exam ended - handle failed, when it suddenly ends because of the time and because of the score limit not being reached
            examAttempt.setStatus(ExamStatus.PASSED);
            examAttempt.setEndTime(LocalDateTime.now());
            examAttemptRepository.save(examAttempt);
            return null;
        }

        var currentQuestion = examAttemptQuestionsRepository.findByExamAttempt_IdAndOrderIndex(
                examAttempt.getId(),
                examAttempt.getCurrentQuestionIndex()
        );

        int correctAnswers = examAttempt.getCurrentQuestionIndex() - examAttempt.getWrongAnswersCounter();

        ExamAttemptResponse.Question examAttemptQuestionResponse = new ExamAttemptResponse.Question(currentQuestion);

        return ExamAttemptResponse.builder()
                .id(examAttempt.getId())
                .start(examAttempt.getStartTime())
                .end(examAttempt.getEndTime())
                .currentQuestionIndex(examAttempt.getCurrentQuestionIndex())
                .status(examAttempt.getStatus().toString())
                .totalQuestions(examConfiguration.getNumberOfQuestions())
                .correctAnswers(correctAnswers)
                .wrongAnswers(examAttempt.getWrongAnswersCounter())
                .currentQuestion(examAttemptQuestionResponse)
                .build();
    }
    public boolean isAttemptPassed(Long examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findById(examAttemptId).orElse(null);
        if(examAttempt == null) {
            throw new RuntimeException("Exam attempt not found");
        }
        return examAttempt.getStatus().equals(ExamStatus.PASSED);
    }

}
