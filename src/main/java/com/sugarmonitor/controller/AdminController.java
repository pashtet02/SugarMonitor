package com.sugarmonitor.controller;

import com.sugarmonitor.model.User;
import com.sugarmonitor.service.impl.UserServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  @PreAuthorize("hasAuthority('ADMIN')")
  public String getAdminPage(@AuthenticationPrincipal User user, Model model) {

    model.addAttribute("user", user);
    return "admin";
  }

  @PostMapping("/add")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String createUser(@AuthenticationPrincipal User userInSession, User user, Model model) {
    UserDetails userFromDB = userService.loadUserByUsername(user.getUsername());
    if (userFromDB != null) {
      throw new RuntimeException(
          "User with username: " + userFromDB.getUsername() + "already exists!");
    }

    List<User> allUsers =
        userService.findAll().stream()
            .filter(u -> !u.getUsername().equals(userInSession.getUsername()))
            .collect(Collectors.toList());
    model.addAttribute("user", user);
    model.addAttribute("allUsers", allUsers);
    return "admin";
  }
}
