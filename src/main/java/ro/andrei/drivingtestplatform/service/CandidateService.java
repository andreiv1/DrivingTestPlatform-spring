package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.model.Candidate;
import ro.andrei.drivingtestplatform.model.ExamConfiguration;
import ro.andrei.drivingtestplatform.repository.CandidateRepository;
import ro.andrei.drivingtestplatform.repository.ExamConfigurationRepository;
import ro.andrei.drivingtestplatform.request.CandidateRequest;
import ro.andrei.drivingtestplatform.response.CandidateResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final ExamConfigurationRepository examConfigurationRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository,
                            ExamConfigurationRepository examConfigurationRepository) {
        this.candidateRepository = candidateRepository;
        this.examConfigurationRepository = examConfigurationRepository;
    }

    public void saveCandidate(CandidateRequest candidateRequest) {
        ExamConfiguration examConfiguration = examConfigurationRepository
                .findById(candidateRequest.getExamConfigId())
                .orElseThrow(() -> new RuntimeException("Exam configuration not found"));

        Candidate candidate;

        if(candidateRequest.getId() == null) {
            candidate = Candidate.builder()
                    .joinDate(LocalDateTime.now())
                    .name(candidateRequest.getName())
                    .cnp(candidateRequest.getCnp())
                    .examConfiguration(examConfiguration)
                    .build();
        } else {
            candidate = candidateRepository.findById(candidateRequest.getId())
                    .map(existingCandidate -> {
                        existingCandidate.setExamConfiguration(examConfiguration);
                        return existingCandidate;
                    })
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));
        }

        candidateRepository.save(candidate);
    }

    public List<CandidateResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAllByOrderByJoinDateDesc();
        return candidates.stream()
                .map(c -> new CandidateResponse(c))
                .collect(Collectors.toList());
    }

    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        return new CandidateResponse(candidate);
    }
}
