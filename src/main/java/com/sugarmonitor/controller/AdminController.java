package com.sugarmonitor.controller;

import com.sugarmonitor.exception.UserNotFoundException;
import com.sugarmonitor.model.Role;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.impl.UserServiceImpl;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final UserServiceImpl userService;

  @GetMapping()
  @PreAuthorize("hasAuthority('ADMIN')")
  public String getAdminPage(@AuthenticationPrincipal User user, Model model) {
    prepareAdminPageModel(model, user);
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
    prepareAdminPageModel(model, userInSession);
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

  @GetMapping("/disable/{username}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String disableUser(
      @PathVariable String username, @AuthenticationPrincipal User userInSession, Model model) {
    prepareAdminPageModel(model, userInSession);
    model.addAttribute("userToBeCreated", new User());

    User user = (User) userService.loadUserByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User with username: " + username + " not found!");
    }

    user.setEnabled(false);

    userService.updateUser(user);
    return "redirect:/admin";
  }

  @GetMapping("/enable/{username}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String enableUser(
      @PathVariable String username, @AuthenticationPrincipal User userInSession, Model model) {
    prepareAdminPageModel(model, userInSession);
    model.addAttribute("userToBeCreated", new User());

    User user = (User) userService.loadUserByUsername(username);
    if (user == null) {
      throw new UserNotFoundException("User with username: " + username + " not found!");
    }

    user.setEnabled(true);

    userService.updateUser(user);
    return "redirect:/admin";
  }

  private void prepareAdminPageModel(Model model, User userInSession) {
    model.addAttribute("user", userInSession);
    List<User> allUsers = userService.findAllUsers();
    model.addAttribute("allUsers", allUsers);
  }
}
