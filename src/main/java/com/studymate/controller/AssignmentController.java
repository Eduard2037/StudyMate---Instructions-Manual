package com.studymate.controller;

import com.studymate.exceptions.DuplicateIdException;
import com.studymate.exceptions.InvalidCourseException;
import com.studymate.model.Assignment;
import com.studymate.service.StudyMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/assignments")
public class AssignmentController {

    private final StudyMateService service;

    @Autowired
    public AssignmentController(StudyMateService service) {
        this.service = service;
    }

    @GetMapping
    public String listAssignments(Model model) {
        model.addAttribute("assignments", service.getAssignments());
        model.addAttribute("courses", service.getCourses());
        model.addAttribute("courseMap", service.getCourseMap());
        model.addAttribute("newAssignment", new Assignment());
        return "assignments";
    }

    @PostMapping
    public String addAssignment(Assignment assignment, RedirectAttributes redirectAttributes) {
        try {
            service.addAssignment(assignment);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment added successfully!");
        } catch (DuplicateIdException | InvalidCourseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding assignment: " + e.getMessage());
        }
        return "redirect:/assignments";
    }
}
