package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.andrei.drivingtestplatform.model.enums.DrivingLicenseType;
import ro.andrei.drivingtestplatform.model.ExamConfiguration;

public interface ExamConfigurationRepository extends JpaRepository<ExamConfiguration, Long> {
    ExamConfiguration findByLicenseType(DrivingLicenseType licenseType);
}
