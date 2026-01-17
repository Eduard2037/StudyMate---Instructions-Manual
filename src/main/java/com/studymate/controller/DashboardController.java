package com.studymate.controller;

import com.studymate.service.StudyMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StudyMateService service;

    @Autowired
    public DashboardController(StudyMateService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("courseCount", service.getCourses().size());
        model.addAttribute("assignmentCount", service.getAssignments().size());
        model.addAttribute("upcomingDeadlines", service.getUpcomingDeadlines());
        return "index";
    }
}
