package com.sugarmonitor.controller;

import com.sugarmonitor.model.User;
import com.sugarmonitor.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final UserServiceImpl userService;

  @GetMapping
  public String getAdminPage(Model model) {
    return "admin";
  }

  @PostMapping("/add")
  public String createUser(User user, Model model) {
    UserDetails userFromDB = userService.loadUserByUsername(user.getUsername());
    if (userFromDB != null) {
      model.addAttribute(
          "error", "User with username: " + userFromDB.getUsername() + "already exists!");
    }

    return "admin";
  }
}
