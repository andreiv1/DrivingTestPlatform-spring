package ro.andrei.drivingtestplatform.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ro.andrei.drivingtestplatform.exceptions.ExamAttemptNotFoundException;
import ro.andrei.drivingtestplatform.util.Logger;

@ControllerAdvice
public class ExceptionHandlingController {
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        Logger.getInstance().error(ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/index";
    }

    @ExceptionHandler(ExamAttemptNotFoundException.class)
    public String handleExamAttemptNotFoundException(ExamAttemptNotFoundException ex, Model model) {
        Logger.getInstance().error(ex.getMessage());
        model.addAttribute("errorMessage", "Examinarea nu este posibila.");
        return "error/index";
    }
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        Logger.getInstance().error(ex.getMessage());
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error/index";
    }
}
