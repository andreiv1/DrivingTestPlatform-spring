package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.andrei.drivingtestplatform.request.QuestionRequest;
import ro.andrei.drivingtestplatform.service.ExamConfigurationService;
import ro.andrei.drivingtestplatform.service.QuestionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final ExamConfigurationService examConfigurationService;

    @Autowired
    public QuestionController(QuestionService questionService, ExamConfigurationService examConfigurationService) {
        this.questionService = questionService;
        this.examConfigurationService = examConfigurationService;
    }

    @GetMapping("/questions")
    public String questions(Model model){
        model.addAttribute("questions", questionService.getQuestions());
        return "questions/index";
    }

    @GetMapping("/questions/add")
    public String addQuestion(Model model){
        var questionRequest = new QuestionRequest();
        questionRequest.setAnswers(new ArrayList<>(List.of("", "", "")));
        questionRequest.setCorrectAnswers(new ArrayList<>(List.of(false,false,false)));

        model.addAttribute("examConfigs", examConfigurationService.getExamConfigurations());
        model.addAttribute("question", questionRequest);
        model.addAttribute("mode", "add");

        return "questions/form";
    }

    @PostMapping("/questions")
    public String saveQuestion(@ModelAttribute QuestionRequest questionRequest){
        List<Boolean> sanitizedCorrectAnswers = questionRequest.getCorrectAnswers().stream()
                .map(correct -> correct != null && correct)
                .collect(Collectors.toList());
        questionRequest.setCorrectAnswers(sanitizedCorrectAnswers);
        questionService.saveQuestion(questionRequest);
        if(questionRequest.getId() == null){
            return "redirect:/questions";
        }
        return "redirect:/questions/view/"+questionRequest.getId();
    }
    @GetMapping("/questions/import")
    public String importQuestions(Model model){
        model.addAttribute("examConfigs", examConfigurationService.getExamConfigurations());

        return "questions/import";
    }

    @PostMapping("/questions/import")
    public String importQuestionsPost(
            @RequestParam("examConfigId") Long examConfigId,
            @RequestParam("file") MultipartFile file) throws IOException {
        questionService.importQuestionsFromFile(file.getInputStream(), examConfigId);
        return "redirect:/questions";
    }

    @GetMapping("/questions/view/{id}")
    public String viewQuestion(Model model, @PathVariable String id){
        model.addAttribute("examConfigs", examConfigurationService.getExamConfigurations());
        model.addAttribute("question", questionService.getQuestion(Long.parseLong(id)));
        model.addAttribute("mode", "view");
        return "questions/form";
    }

}
