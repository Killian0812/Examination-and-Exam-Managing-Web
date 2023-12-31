package com.killian.SpringBoot.ExamApp.controllers.views;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.killian.SpringBoot.ExamApp.models.Exam;
import com.killian.SpringBoot.ExamApp.models.Question;
import com.killian.SpringBoot.ExamApp.repositories.ExamRepository;
import com.killian.SpringBoot.ExamApp.repositories.QuestionRepository;

@Controller
public class ExamViewController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/create-exam-page")
    public ModelAndView createQuestionPage(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        List<String> subjects = questionRepository.findDistinctSubjects();
        ModelAndView modelAndView = new ModelAndView("create-exam.html");
        modelAndView.addObject("subjects", subjects);
        modelAndView.addObject("selectedSubject", subjects.get(0));
        modelAndView.addObject("username", username);
        modelAndView.addObject("password", password);
        return modelAndView;
    }

    @PostMapping("/create-exam")
    public ModelAndView createQuestion(
            @RequestParam("name") String name,
            @RequestParam("selectedSubject") String subject,
            @RequestParam("easyQuestionCount") int easyQuestionCount,
            @RequestParam("mediumQuestionCount") int mediumQuestionCount,
            @RequestParam("hardQuestionCount") int hardQuestionCount,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
                
        ModelAndView modelAndView = new ModelAndView("create-exam.html");

        Exam newExam = new Exam();
        newExam.setName(name);
        newExam.setSubject(subject);

        List<Question> easyQuestions = questionRepository.findRandomEasyQuestions(easyQuestionCount);
        List<Question> mediumQuestions = questionRepository.findRandomMediumQuestions(mediumQuestionCount);
        List<Question> hardQuestions = questionRepository.findRandomHardQuestions(hardQuestionCount);

        List<Question> questions = Stream.of(easyQuestions, mediumQuestions, hardQuestions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        newExam.setQuestions(questions);

        String message = null;
        try {
            examRepository.save(newExam);
            message = "Successful! Exam added to database.";
        } catch (Exception e) {
            message = "Failed! Exam name is taken.";
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("username", username);
        modelAndView.addObject("password", password);
        return modelAndView;
    }

    @GetMapping("/view-exams-by-filter-page")
    public ModelAndView getQuestionsByFilterPage(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        ModelAndView modelAndView = new ModelAndView("exams-by-filter.html");

        modelAndView.addObject("username", username);
        modelAndView.addObject("password", password);

        List<String> subjects = examRepository.findDistinctSubjects();
        modelAndView.addObject("subjects", subjects);

        // Initially, display questions from the first subject
        if (!subjects.isEmpty())
            modelAndView.addObject("selectedSubject", subjects.get(0));

        return modelAndView;
    }

    @GetMapping("/get-exams-by-subject")
    public ModelAndView getExamsBySelectedSubject(
            @RequestParam("selectedSubject") String selectedSubject,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        ModelAndView modelAndView = new ModelAndView("exams-by-filter.html");
        // Retrieve questions based on the selected subject and difficulty

        List<String> subjects = examRepository.findDistinctSubjects();

        List<Exam> exams = examRepository.findBySubject(selectedSubject);
        List<String> examNames = exams.stream()
                .map(Exam::getName)
                .collect(Collectors.toList());

        modelAndView.addObject("subjects", subjects);
        modelAndView.addObject("selectedSubject", selectedSubject);
        modelAndView.addObject("examNames", examNames);
        modelAndView.addObject("username", username);
        modelAndView.addObject("password", password);

        return modelAndView;
    }

    @GetMapping("/get-exam-by-name/{name}")
    public ModelAndView viewExam(
            @PathVariable String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        ModelAndView modelAndView = new ModelAndView("exam-by-name.html");
        Exam exam = examRepository.findByName(name);

        modelAndView.addObject("exam", exam);
        modelAndView.addObject("username", username);
        modelAndView.addObject("password", password);

        return modelAndView;
    }
}
