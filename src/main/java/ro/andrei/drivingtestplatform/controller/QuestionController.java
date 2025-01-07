package ro.andrei.drivingtestplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuestionController {
    @GetMapping("/questions")
    public String questions(){

        return "questions";
    }
}
