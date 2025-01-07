package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ro.andrei.drivingtestplatform.request.CandidateRequest;
import ro.andrei.drivingtestplatform.service.ExamService;

@Controller
public class CandidateController {
    private final ExamService examService;

    @Autowired
    public CandidateController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/candidates")
    public String candidates(){
        return "candidates/index";
    }

    @GetMapping("/candidates/add")
    public String addCandidate(Model model){
        model.addAttribute("examConfigs", examService.getExamConfigurations());
        model.addAttribute("mode", "add");

        return "candidates/form";
    }

    @PostMapping("/candidates/add")
    public String addCandidatePost(Model model, CandidateRequest candidateRequest){

        return "redirect:/candidates";
    }

    @GetMapping("/candidates/view/{id}")
    public String viewCandidate(){
        return "candidates/view";
    }


}
