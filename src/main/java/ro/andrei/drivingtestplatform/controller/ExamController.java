package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ro.andrei.drivingtestplatform.model.DrivingLicenseType;
import ro.andrei.drivingtestplatform.service.ExamService;

@Controller
public class ExamController {
    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exam")
    public String home(){
        return "redirect:/";
    }

    @PostMapping("/exam/generate")
    public String generateExamForCandidate(@RequestParam("candidateId") Long candidateId) {
        examService.generateExam(candidateId);
        return "redirect:/candidates/view/" + candidateId;
    }
    @PostMapping("/exam")
    public String startExam(@RequestParam("cnp") String cnp) {
        System.out.println("Starting exam for candidate with CNP: " + cnp);

        return "redirect:/exam";
    }

    @PostMapping("/exam/next")
    public String nextQuestion() {
        return "redirect:/exam";
    }

}
