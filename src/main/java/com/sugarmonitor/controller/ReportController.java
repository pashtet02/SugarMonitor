package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.service.GraphService;
import com.sugarmonitor.service.ProfileService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

  private final GraphService graphService;

  private final ProfileService profileService;

  @GetMapping()
  public String returnReportPage(Model model) {
    return "report";
  }

  @GetMapping("/daytoday")
  public String mainReportMenu(
      @RequestParam(name = "generateFor", required = false, defaultValue = "") String generateFor,
      @RequestParam(name = "fromDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDate,
      @RequestParam(name = "toDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date toDate,
      Model model) {
    Date from;
    Date to;
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    if (generateFor.equals("today")) {
      from = cal.getTime();
      to = new Date();
    } else if (generateFor.equals("2days")) {
      cal.add(Calendar.DATE, -1);
      from = cal.getTime();
      to = new Date();
    } else if (generateFor.equals("week")) {
      cal.add(Calendar.DATE, -7);
      from = cal.getTime();
      to = new Date();
    } else if (generateFor.equals("month")) {
      cal.add(Calendar.MONTH, -1);
      cal.getTime();
      from = cal.getTime();
      to = new Date();
    } else {
      if (fromDate == null) {
        // set default value as 7 days before current time
        cal.add(Calendar.DATE, -7);
        from = cal.getTime();
      } else {
        from = fromDate;
      }

      if (toDate == null) {
        to = cal.getTime();
      } else to = toDate;
    }

    Profile activeProfile = profileService.getProfile();
    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());
    Map<String, Map<String, Double>> map = groupEntriesByDay(data, activeProfile);

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("map", map);
    return "/report";
  }

  public Map<String, Map<String, Double>> groupEntriesByDay(
      List<Entry> entries, Profile activeProfile) {
    Map<String, Map<String, Double>> result = new LinkedHashMap<>();
    for (Entry entry : entries) {
      String day = entry.getDateString().replace("T", " ").split(" ")[0];
      result
          .computeIfAbsent(day, k -> new LinkedHashMap<>())
          .put(
              graphService.convertEntryDateIntoStringOnGraph(entry),
              entry.getSgv(activeProfile.getUnits()));
    }

    return result;
  }
}
