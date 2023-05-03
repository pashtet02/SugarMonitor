package com.sugarmonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

  @GetMapping("/access-denied")
  public String getErrorPage(Model model) {
    model.addAttribute("error", "Sorry, you are not authorized to access this page.");
    return "error";
  }
}
