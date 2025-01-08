package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.repository.ExamAttemptRepository;
import ro.andrei.drivingtestplatform.response.ExamAttemptListingRespose;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamAttemptService {
    private final ExamAttemptRepository examAttemptRepository;

    @Autowired
    public ExamAttemptService(ExamAttemptRepository examAttemptRepository) {
        this.examAttemptRepository = examAttemptRepository;
    }

    public List<ExamAttemptListingRespose> getExamAttemptsByCandidateId(Long candidateId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return examAttemptRepository.findAllByCandidate_Id(candidateId).stream().map(
                    e -> new ExamAttemptListingRespose(
                            e.getId(),
                            e.getStartTime() != null ?
                            e.getStartTime().format(formatter) : "",
                            e.getEndTime() != null ?
                            e.getEndTime().format(formatter) : "",
                            e.getLicenseType().toString(),
                            e.getStatus().toString()
                    )
                )
                .collect(Collectors.toList());
    }

}
