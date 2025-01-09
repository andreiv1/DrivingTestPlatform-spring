package ro.andrei.drivingtestplatform.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRequest {
    private Long id;
    private String name;
    private String cnp;
    private Long examConfigId;
}
