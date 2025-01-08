package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptListingRespose {
    private Long id;
    private String startDate;
    private String endDate;
    private String licenseType;
    private String status;
}
