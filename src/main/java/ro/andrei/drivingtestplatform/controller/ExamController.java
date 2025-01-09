package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ro.andrei.drivingtestplatform.exceptions.ExamAttemptNotFoundException;
import ro.andrei.drivingtestplatform.response.ExamAttemptResponse;
import ro.andrei.drivingtestplatform.service.ExamAttemptService;

import java.util.List;

@Controller
public class ExamController {
    private final ExamAttemptService examService;

    @Autowired
    public ExamController(ExamAttemptService examService) {
        this.examService = examService;

    }

    @GetMapping("/exam")
    public String home(){
        return "redirect:/";
    }

    @PostMapping("/exam/generate")
    public String generateExamForCandidate(@RequestParam("candidateId") Long candidateId) {
        examService.generate(candidateId);
        return "redirect:/candidates/view/" + candidateId;
    }
    @PostMapping("/exam/start")
    public String startExam(@RequestParam("cnp") String cnp,
                            Model model) throws ExamAttemptNotFoundException {
        var response = examService.start(cnp);
        model.addAttribute("examAttempt", response);
        return "exam/index";

    }

    @PostMapping("/exam/submit")
    public String submitExamAnswers(@RequestParam("examAttemptId") Long examAttemptId,
                                    @RequestParam("questionId") Long questionId,
                                    @RequestParam(name = "selectedAnswers", required = false) List<Long> selectedAnswers,
                                    Model model) throws ExamAttemptNotFoundException {
        if(selectedAnswers == null || selectedAnswers.isEmpty()){
            model.addAttribute("error", "Please select at least one answer");
            return "exam/index";
        }

        ExamAttemptResponse response =
                examService.continueExam(examAttemptId, questionId, selectedAnswers);

        if(response == null) {
            return "redirect:/exam/finish?examAttemptId=" + examAttemptId;
        }
        model.addAttribute("examAttempt", response);
        return "exam/index";
    }

    @GetMapping("/exam/finish")
    public String finishExam(@RequestParam("examAttemptId") Long examAttemptId,
                             Model model) {
        model.addAttribute("isPassed", examService.isAttemptPassed(examAttemptId));
        return "exam/finish";
    }

}
