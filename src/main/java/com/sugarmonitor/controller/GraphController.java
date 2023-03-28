package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GraphController {

  private final EntryRepository entryRepository;

  @GetMapping("/displayBarGraph")
  public String barGraph(
      @RequestParam(required = false, defaultValue = "2") Long displayForLast, Model model) {
    Map<String, Double> map = new LinkedHashMap<>();
    Set<Integer> hours = new LinkedHashSet<>();

    Date from = Date.from(Instant.now().minus(displayForLast, ChronoUnit.HOURS));
    Date to = Date.from(Instant.now());

    List<Entry> data = entryRepository.findByDateBetween(from.getTime(), to.getTime());
    data.forEach(
        entry -> {
          map.put(convertEntryDateIntoStringOnGraph(entry), entry.getSgvInMmol());
        });

    List<Entry> xAxisValues =
        data.stream()
            .sorted((Comparator.comparingLong(Entry::getDate)))
            .collect(Collectors.toList());

    model.addAttribute("sugarMap", map);
    model.addAttribute("xAxisValues", xAxisValues);
    model.addAttribute("lowSugarLine", 3.9);
    model.addAttribute("highSugarLine", 10);
    return "barGraph";
  }

  @GetMapping("/chart")
  public String chart() {

    return "highchart";
  }

  public String convertEntryDateIntoStringOnGraph(Entry entry) {
    LocalDateTime localDateTime =
        new Date(entry.getDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    String dayOfMonth = null;
    if (localDateTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
      dayOfMonth =
          localDateTime.getDayOfMonth() + " " + localDateTime.getMonth().toString().substring(0, 3);
    }

    int hour = localDateTime.getHour();
    // To avoid time like 2:1 or 14:6 or 2:55
    String hourStr = String.valueOf(hour);
    if (hour < 10) {
      hourStr = "0" + hourStr;
    }
    int minute = localDateTime.getMinute();
    // To avoid time like 2:1 or 14:6 or 2:55
    String minuteStr = String.valueOf(minute);
    if (minute < 10) {
      minuteStr = "0" + minuteStr;
    }

    String result = hourStr + ":" + minuteStr;
    if (dayOfMonth != null) {
      result = dayOfMonth + " " + result;
    }
    return result;
  }
}
