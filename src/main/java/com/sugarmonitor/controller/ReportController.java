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
    Calendar fromCal = Calendar.getInstance();
    fromCal.set(Calendar.HOUR_OF_DAY, 0);
    fromCal.set(Calendar.MINUTE, 0);
    fromCal.set(Calendar.SECOND, 0);

    Calendar toCal = Calendar.getInstance();
    toCal.set(Calendar.HOUR_OF_DAY, 23);
    toCal.set(Calendar.MINUTE, 59);
    toCal.set(Calendar.SECOND, 59);
    if (generateFor.equals("today")) {
      from = fromCal.getTime();
      to = new Date();
    } else if (generateFor.equals("2days")) {
      fromCal.add(Calendar.DATE, -1);
      from = fromCal.getTime();
      to = new Date();
    } else if (generateFor.equals("week")) {
      fromCal.add(Calendar.DATE, -7);
      from = fromCal.getTime();
      to = toCal.getTime();
    } else if (generateFor.equals("month")) {
      fromCal.add(Calendar.MONTH, -1);
      fromCal.getTime();
      from = fromCal.getTime();
      to = toCal.getTime();
    } else {
      if (fromDate == null) {
        // set default value as 7 days before current time
        fromCal.add(Calendar.DATE, -7);
        from = fromCal.getTime();
      } else {
        from = fromDate;
      }

      if (toDate == null) {
        to = fromCal.getTime();
      } else to = toCal.getTime();
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
