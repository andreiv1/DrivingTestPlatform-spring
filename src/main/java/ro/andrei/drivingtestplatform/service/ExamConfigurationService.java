package ro.andrei.drivingtestplatform.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.andrei.drivingtestplatform.model.enums.DrivingLicenseType;
import ro.andrei.drivingtestplatform.model.ExamConfiguration;
import ro.andrei.drivingtestplatform.repository.ExamConfigurationRepository;
import ro.andrei.drivingtestplatform.response.ExamConfigurationResponse;

import java.util.List;

@Service
public class ExamConfigurationService {
    private final ExamConfigurationRepository examConfigurationRepository;
    @Autowired
    public ExamConfigurationService(ExamConfigurationRepository examConfigurationRepository) {
        this.examConfigurationRepository = examConfigurationRepository;
    }

    @PostConstruct
    private void init() {
        List<ExamConfiguration> examConfigurations = List.of(
                new ExamConfiguration(null, DrivingLicenseType.A, 20, 20, 17),
                new ExamConfiguration(null, DrivingLicenseType.B, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.C, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.D, 30, 26, 22),
                new ExamConfiguration(null, DrivingLicenseType.E, 15, 11, 9),
                new ExamConfiguration(null, DrivingLicenseType.R, 20, 15, 13)
        );
        //Check if the exam configurations are already in the database
        if (examConfigurationRepository.count() == 0) {
            examConfigurationRepository.saveAll(examConfigurations);
        }
    }

    public List<ExamConfigurationResponse> getExamConfigurations() {
        return examConfigurationRepository.findAll()
                .stream().map(e -> new ExamConfigurationResponse(e.getId(),e.getLicenseType().toString())).toList();
    }
}
