package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.service.GraphService;
import com.sugarmonitor.service.ProfileService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
  public String returnReportPage() {
    return "report";
  }

  @GetMapping("/daytoday")
  public String mainReportMenu(
      @RequestParam(name = "today", required = false) boolean today,
      @RequestParam(name = "for2days", required = false) boolean for2days,
      @RequestParam(name = "fromDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDate,
      @RequestParam(name = "toDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date toDate,
      Model model) {
    Date from = null;
    Date to = null;
    if (today) {
      from = new Date();
      from.setHours(0);
      from.setMinutes(0);
      from.setSeconds(0);
      to = new Date();
    } else if (for2days) {
      // set default value as 7 days before current time
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -2);
      from = cal.getTime();
      to = new Date();
    } else {
      if (fromDate == null) {
        // set default value as 7 days before current time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        fromDate = cal.getTime();
      }

      if (toDate == null) {
        // set default value as 7 days before current time
        Calendar cal = Calendar.getInstance();
        toDate = cal.getTime();
      }
    }

    fillModelWithGraphData(model, from, to);
    return "report";
  }
  // List<Map<String, Map<String, Double>>> list = new LinkedList<>();
  /*I have a List<Entry> entries I need a List<Map<String, Map<String, Double>>>
  where Map<String, Double> it is an each Entry graphService.convertEntryDateIntoStringOnGraph(entry) as a String (key)
  and entry.getSgv(activeProfile.getUnits()) as a value
  then Map<String, Map<String, Double>
  where String is a */
  public Map<String, Map<String, Double>> groupEntriesByDay(
      List<Entry> entries, Profile activeProfile) {
    Map<String, Map<String, Double>> result = new LinkedHashMap<>();
    for (Entry entry : entries) {
      LocalDate date =
          Instant.ofEpochMilli(entry.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
      String day = date.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
      result
          .computeIfAbsent(day, k -> new LinkedHashMap<>())
          .put(
              graphService.convertEntryDateIntoStringOnGraph(entry),
              entry.getSgv(activeProfile.getUnits()));
    }

    return result;
  }

  private void fillModelWithGraphData(Model model, Date from, Date to) {
    List<Map<String, Double>> list = new LinkedList<>();
    Profile activeProfile = profileService.getProfile();
    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());
    Map<String, Map<String, Double>> map2 = groupEntriesByDay(data, activeProfile);
    Map<String, Double> map = new LinkedHashMap<>();
    list.add(map);

    data.forEach(
        entry ->
            map.put(
                graphService.convertEntryDateIntoStringOnGraph(entry),
                entry.getSgv(activeProfile.getUnits())));

    // model.addAttribute("sugarMap", map);
    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("list", list);
    model.addAttribute("map", map2);
  }
}
