package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ro.andrei.drivingtestplatform.request.CandidateRequest;
import ro.andrei.drivingtestplatform.service.CandidateService;
import ro.andrei.drivingtestplatform.service.ExamAttemptService;
import ro.andrei.drivingtestplatform.service.ExamService;

@Controller
public class CandidateController {
    private final ExamService examService;

    private final ExamAttemptService examAttemptService;
    private final CandidateService candidateService;

    @Autowired
    public CandidateController(ExamService examService, ExamAttemptService examAttemptService, CandidateService candidateService) {
        this.examService = examService;
        this.examAttemptService = examAttemptService;
        this.candidateService = candidateService;
    }

    @GetMapping("/candidates")
    public String candidates(Model model){
        model.addAttribute("candidates", candidateService.getAllCandidates());
        return "candidates/index";
    }

    @GetMapping("/candidates/add")
    public String addCandidate(Model model){
        model.addAttribute("examConfigs", examService.getExamConfigurations());
        model.addAttribute("mode", "add");

        return "candidates/form";
    }

    @PostMapping("/candidates/add")
    public String addCandidatePost(CandidateRequest candidateRequest){
        candidateService.addCandidate(candidateRequest);
        return "redirect:/candidates";
    }

    @GetMapping("/candidates/view/{id}")
    public String viewCandidate(Model model, @PathVariable String id){
        model.addAttribute("mode", "view");
        model.addAttribute("examConfigs", examService.getExamConfigurations());
        model.addAttribute("candidate", candidateService.getCandidateById(Long.parseLong(id)));
        model.addAttribute("examAttempts", examAttemptService.getExamAttemptsByCandidateId(Long.parseLong(id)));
        return "candidates/form";
    }


}
