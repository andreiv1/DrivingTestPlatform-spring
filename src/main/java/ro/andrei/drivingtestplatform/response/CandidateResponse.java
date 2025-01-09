package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.Candidate;

import java.time.format.DateTimeFormatter;

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
    private String examConfigurationLicenseType;

    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.cnp = candidate.getCnp();
        this.joinDate = candidate.getJoinDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();
        this.examConfigurationId = candidate.getExamConfiguration().getId();
        this.examConfigurationLicenseType = candidate.getExamConfiguration().getLicenseType().toString();
    }
}
