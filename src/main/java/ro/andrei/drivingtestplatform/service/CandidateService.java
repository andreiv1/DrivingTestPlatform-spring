package ro.andrei.drivingtestplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.model.Candidate;
import ro.andrei.drivingtestplatform.repository.CandidateRepository;
import ro.andrei.drivingtestplatform.request.CandidateRequest;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public void addCandidate(CandidateRequest candidateRequest) {
        // Add candidate to database
//        Candidate candidate = new Candidate(null, candidateRequest.getName(), candidateRequest.getCnp(), candidateRequest.getE);
    }
}
