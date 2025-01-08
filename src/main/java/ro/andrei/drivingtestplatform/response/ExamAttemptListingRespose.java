package ro.andrei.drivingtestplatform.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.ExamAttempt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ExamAttemptListingRespose {
    private Long id;
    private String startTime;
    private String endTime;
    private String licenseType;
    private String status;

    public ExamAttemptListingRespose(ExamAttempt e){
        setId(e.getId());
        setStartTime(e.getStartTime());
        setEndTime(e.getEndTime());
        setLicenseType(e.getLicenseType().toString());
        setStatus(e.getStatus().name());
    }


    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            this.startTime = "-";
        } else {
            this.startTime = startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null) {
            this.endTime = "-";
        } else {
            this.endTime = endTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    public void setStatus(String status) {
        switch (status) {
            case "NOT_STARTED":
                this.status = "Nu a început";
                break;
            case "IN_PROGRESS":
                this.status = "În progres";
                break;
            case "FAILED":
                this.status = "RESPINS";
                break;
            case "PASSED":
                this.status = "Admis";
                break;
            default:
                this.status = "Necunoscut";
        }
    }
}
