package com.sugarmonitor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReportController {

  @GetMapping("/report")
  public String mainReportMenu() {
    return "report";
  }
}
