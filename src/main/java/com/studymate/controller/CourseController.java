package com.studymate.controller;

import com.studymate.exceptions.DuplicateIdException;
import com.studymate.model.Course;
import com.studymate.service.StudyMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final StudyMateService service;

    @Autowired
    public CourseController(StudyMateService service) {
        this.service = service;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", service.getCourses());
        model.addAttribute("newCourse", new Course());
        return "courses";
    }

    @PostMapping
    public String addCourse(Course course, RedirectAttributes redirectAttributes) {
        try {
            service.addCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        } catch (DuplicateIdException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding course: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}
