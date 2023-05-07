package com.sugarmonitor.controller;

import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleException(Exception ex, Model model) {
    log.error("handleException: message {}", ex.getMessage());
    model.addAttribute("error", ex.getMessage());
    return "error";
  }

  @ExceptionHandler({AuthenticationException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public String handleAuthenticationException(Exception ex, Model model) {
    log.error("handleException: message {}", ex.getMessage());
    model.addAttribute("error", "You are not allowed to view this page!");
    return "error";
  }
}
