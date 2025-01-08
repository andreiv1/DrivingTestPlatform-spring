package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ro.andrei.drivingtestplatform.service.ExamService;

import java.util.List;

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
    @PostMapping("/exam/start")
    public String startExam(@RequestParam("cnp") String cnp,
                            Model model) {

        var response = examService.startExam(cnp);
        model.addAttribute("examAttempt", response);
        return "exam/index";
    }

    @PostMapping("/exam/submit")
    public String submitExamAnswers(@RequestParam("examAttemptId") Long examAttemptId,
                                    @RequestParam("questionId") Long questionId,
                                    @RequestParam(name = "selectedAnswers", required = false) List<Long> selectedAnswers,
                                    Model model) {
        if(selectedAnswers == null || selectedAnswers.isEmpty()){
            model.addAttribute("error", "Please select at least one answer");
            return "exam/index";
        }


        examService.saveExamAttemptAnswer(examAttemptId, questionId, selectedAnswers);

        var response = examService.getNextQuestion(examAttemptId);
        if(response == null) {
            return "redirect:/exam/finish?examAttemptId=" + examAttemptId;
        }
        model.addAttribute("examAttempt", response);
        return "exam/index";
    }

    @GetMapping("/exam/finish")
    public String finishExam(@RequestParam("examAttemptId") Long examAttemptId,
                             Model model) {

        return "exam/finish";
    }

}
