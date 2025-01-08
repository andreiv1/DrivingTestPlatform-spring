package ro.andrei.drivingtestplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.andrei.drivingtestplatform.model.DrivingLicenseType;
import ro.andrei.drivingtestplatform.model.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions WHERE license_type = ? ORDER BY RANDOM() LIMIT ?", nativeQuery = true)
    List<Question> getRandQuestionsByLicenseType(@Param("license_type") String licenseType,
                                                 @Param("count") int count);

    int countByDrivingLicenseType(@Param("license_type") DrivingLicenseType licenseType);

}
