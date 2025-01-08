package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.ExamAttempt;

import java.time.format.DateTimeFormatter;

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

    public ExamAttemptListingRespose(ExamAttempt e){
        this.id = e.getId();
        this.startDate = e.getStartTime() == null ? "Not started" : e.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.endDate = e.getEndTime() == null ? "Not finished" : e.getEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.licenseType = e.getLicenseType().toString();
        this.status = e.getStatus().name();
    }
}
