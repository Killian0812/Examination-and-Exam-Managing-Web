package com.killian.SpringBoot.ExamApp.controllers.viewcontrollers.student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.killian.SpringBoot.ExamApp.models.Classroom;
import com.killian.SpringBoot.ExamApp.models.StudentClassroom;
import com.killian.SpringBoot.ExamApp.repositories.ClassroomRepository;
import com.killian.SpringBoot.ExamApp.repositories.StudentClassroomRepository;
import com.killian.SpringBoot.ExamApp.services.SessionManagementService;

@Controller
@RequestMapping(path = "/student/classroom")
public class StudentClassroomController {

    @Autowired
    private SessionManagementService sessionManagementService;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private StudentClassroomRepository studentClassroomRepository;

    @GetMapping("/classrooms-page")
    public String classroomPage(Model model) {
        List<StudentClassroom> studentClassrooms = studentClassroomRepository
                .findAllClassByStudent(sessionManagementService.getUsername());
        List<String> classCodes = studentClassrooms.stream()
                .map(StudentClassroom::getClassCode)
                .collect(Collectors.toList());
        List<Classroom> classrooms = new ArrayList<>();
        for (String code : classCodes) {
            classrooms.addAll(classroomRepository.findByClasscode(code));
        }
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("message", sessionManagementService.getMessage());
        sessionManagementService.clearMessage();
        return "student/classrooms";
    }

    @GetMapping("/view-classroom")
    public String viewClassroom(
            @RequestParam("classCode") String classCode,
            Model model) {
        Classroom classroom = classroomRepository.findByClasscode(classCode).get(0);
        model.addAttribute("classroom", classroom);
        model.addAttribute("message", sessionManagementService.getMessage());
        sessionManagementService.clearMessage();
        return "student/view-classroom";
    }

    @GetMapping("/join-classroom-page")
    public String joinClassroomPage(
            Model model) {
        return "student/join-classroom";
    }

    @PostMapping("/join-classroom")
    public String joinClassroom(
            @RequestParam("classCode") String classCode,
            Model model) {
        List<Classroom> classrooms = classroomRepository.findByClasscode(classCode);
        String student = sessionManagementService.getUsername();
        if (!classrooms.isEmpty()) {
            Classroom classroom = classrooms.get(0);
            StudentClassroom studentClassroom = studentClassroomRepository.findRecordByClasscode(student, classCode);
            if (studentClassroom != null) {
                sessionManagementService.setMessage("Bạn đã tham gia lớp từ trước đó.");
            } else {
                studentClassroomRepository.save(new StudentClassroom(student, classroom.getName(), classCode));
                sessionManagementService.setMessage("Bạn đã tham gia lớp: " + classroom.getName());
            }
        } else {
            sessionManagementService.setMessage("Không tồn tại lớp với mã đã cung cấp.");
        }
        return "redirect:/student/classroom/classrooms-page";
    }

}
