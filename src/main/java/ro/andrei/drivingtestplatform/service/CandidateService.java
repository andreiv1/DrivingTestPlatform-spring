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
    public CandidateService(CandidateRepository candidateRepository, ExamConfigurationRepository examConfigurationRepository) {
        this.candidateRepository = candidateRepository;
        this.examConfigurationRepository = examConfigurationRepository;
    }

    public void addCandidate(CandidateRequest candidateRequest) {

        ExamConfiguration examConfiguration = examConfigurationRepository.findById(candidateRequest.getExamConfigId())
                .orElseThrow(() -> new RuntimeException("Exam configuration not found"));

        Candidate candidate = new Candidate();
        candidate.setJoinDate(LocalDate.now());
        candidate.setName(candidateRequest.getName());
        candidate.setCnp(candidateRequest.getCnp());
        candidate.setExamConfiguration(examConfiguration);

        candidateRepository.save(candidate);
    }

    public List<CandidateResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        return candidates.stream()
                .map(candidate ->  new CandidateResponse(
                        candidate.getId(),
                        candidate.getName(),
                        candidate.getCnp(),
                        candidate.getJoinDate().toString(),
                        candidate.getExamConfiguration().getId()
                ))
                .collect(Collectors.toList());
    }

    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        return new CandidateResponse(
                candidate.getId(),
                candidate.getName(),
                candidate.getCnp(),
                candidate.getJoinDate().toString(),
                candidate.getExamConfiguration().getId());
    }
}
