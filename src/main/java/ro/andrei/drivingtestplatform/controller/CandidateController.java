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
import ro.andrei.drivingtestplatform.service.ExamConfigurationService;

@Controller
public class CandidateController {
    private final ExamConfigurationService examConfigurationService;
    private final ExamAttemptService examAttemptService;
    private final CandidateService candidateService;

    @Autowired
    public CandidateController(ExamAttemptService examService, ExamConfigurationService examConfigurationService, ExamAttemptService examAttemptService, CandidateService candidateService) {
        this.examConfigurationService = examConfigurationService;
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
        model.addAttribute("examConfigs", examConfigurationService.getExamConfigurations());
        model.addAttribute("mode", "add");

        return "candidates/form";
    }

    @PostMapping("/candidates")
    public String saveCandidate(CandidateRequest candidateRequest){
        candidateService.saveCandidate(candidateRequest);
        if(candidateRequest.getId() != null){
            return "redirect:/candidates/view/" + candidateRequest.getId();
        }
        return "redirect:/candidates";
    }

    @GetMapping("/candidates/view/{id}")
    public String viewCandidate(Model model, @PathVariable String id){
        model.addAttribute("mode", "view");
        model.addAttribute("examConfigs", examConfigurationService.getExamConfigurations());
        model.addAttribute("candidate", candidateService.getCandidateById(Long.parseLong(id)));
        model.addAttribute("examAttempts", examAttemptService.getExamAttemptsByCandidateId(Long.parseLong(id)));
        return "candidates/form";
    }


}
