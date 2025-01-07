package ro.andrei.drivingtestplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ro.andrei.drivingtestplatform.request.QuestionRequest;
import ro.andrei.drivingtestplatform.service.ExamService;
import ro.andrei.drivingtestplatform.service.QuestionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QuestionController {
    private final ExamService examService;
    private final QuestionService questionService;

    @Autowired
    public QuestionController(ExamService examService, QuestionService questionService) {
        this.examService = examService;
        this.questionService = questionService;
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

        model.addAttribute("examConfigs", examService.getExamConfigurations());
        model.addAttribute("question", questionRequest);
        model.addAttribute("mode", "add");

        return "questions/form";
    }

    @PostMapping("/questions/add")
    public String addQuestionPost(@ModelAttribute QuestionRequest questionRequest){
        questionService.addQuestion(questionRequest);
        return "redirect:/questions";
    }
    @GetMapping("/questions/import")
    public String importQuestions(Model model){
        model.addAttribute("examConfigs", examService.getExamConfigurations());

        return "questions/import";
    }

    @PostMapping("/questions/import")
    public String importQuestionsPost(
            @RequestParam("examConfigId") Long examConfigId,
            @RequestParam("file") MultipartFile file) throws IOException {
        questionService.importQuestionsFromCsv(file.getInputStream(), examConfigId);
        return "redirect:/questions";
    }

}
