package ro.andrei.drivingtestplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.andrei.drivingtestplatform.model.enums.DrivingLicenseType;

import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private DrivingLicenseType drivingLicenseType;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<Answer> answers;

    @Lob
    @Column(name = "imageBlob")
    private byte[] image;
}
