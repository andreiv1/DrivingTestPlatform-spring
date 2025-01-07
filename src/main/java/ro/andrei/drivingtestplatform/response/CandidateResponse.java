package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
