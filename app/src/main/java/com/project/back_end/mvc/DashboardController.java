package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.TokenService; // correct package

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token, Model model) {
        if (tokenService.validateToken(token, "admin")) { // use validateToken
            model.addAttribute("token", token);
            return "admin/adminDashboard"; // Thymeleaf template
        } else {
            return "redirect:/"; // go back to login page
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token, Model model) {
        if (tokenService.validateToken(token, "doctor")) { // use validateToken
            model.addAttribute("token", token);
            return "doctor/doctorDashboard"; // Thymeleaf template
        } else {
            return "redirect:/"; // go back to login page
        }
    }
}