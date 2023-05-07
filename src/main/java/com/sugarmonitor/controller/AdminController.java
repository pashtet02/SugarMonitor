package com.sugarmonitor.controller;

import com.sugarmonitor.model.Role;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.impl.UserServiceImpl;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final UserServiceImpl userService;

  @GetMapping()
  @PreAuthorize("hasAuthority('ADMIN')")
  public String getAdminPage(@AuthenticationPrincipal User user, Model model) {

    model.addAttribute("user", user);
    model.addAttribute("userToBeCreated", new User());
    return "admin";
  }

  @PostMapping("/add")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String addUser(
      @AuthenticationPrincipal User userInSession,
      @ModelAttribute("userToBeCreated") @Valid User userToBeCreated,
      BindingResult bindingResult,
      Model model) {
    model.addAttribute("user", userInSession);

    if (bindingResult.hasErrors()) {
      return "admin";
    }

    if (!userToBeCreated.getPassword().equals(userToBeCreated.getConfirmPassword())) {
      model.addAttribute("confirmPasswordError", "Passwords do not match");
      return "admin";
    }

    userService.addUser(userToBeCreated, Role.USER);
    return "redirect:/admin";
  }
}
