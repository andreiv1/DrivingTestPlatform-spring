package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.Candidate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateResponse {
    private Long id;
    private String name;
    private String cnp;
    private String joinDate;
    private Long examConfigurationId;

    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.cnp = candidate.getCnp();
        this.joinDate = candidate.getJoinDate().toString();
        this.examConfigurationId = candidate.getExamConfiguration().getId();
    }
}
