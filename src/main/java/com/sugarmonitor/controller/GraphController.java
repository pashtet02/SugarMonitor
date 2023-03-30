package com.sugarmonitor.controller;

import com.sugarmonitor.model.DeviceStatus;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.DeviceStatusRepository;
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

  private final DeviceStatusRepository deviceStatusRepository;

  @GetMapping("/displayBarGraph")
  public String barGraph(
      @RequestParam(required = false, defaultValue = "2") Long displayForLast, Model model) {
    Map<String, Double> map = new LinkedHashMap<>();

    Date from = Date.from(Instant.now().minus(displayForLast, ChronoUnit.HOURS));
    Date to = Date.from(Instant.now());

    List<Entry> data = entryRepository.findByDateBetween(from.getTime(), to.getTime());
    data.forEach(entry -> map.put(convertEntryDateIntoStringOnGraph(entry), entry.getSgvInMmol()));

    model.addAttribute("sugarMap", map);
    model.addAttribute("lowSugarLine", 3.9);
    model.addAttribute("highSugarLine", 10);

    // find two last Entry readings and get their diff in value to see trend (like +0.1 mmol,
    // -0.6mmol)
    Entry secondLastReading =
        data.stream()
            .sorted(Comparator.comparing(Entry::getDate))
            .collect(Collectors.toList())
            .get(data.size() - 2);

    data.stream()
        .max(Comparator.comparing(Entry::getDate))
        .ifPresent(
            entry -> {
              model.addAttribute(
                  "lastReadingValue",
                  String.format("%,.1f", entry.getSgvInMmol() - secondLastReading.getSgvInMmol()));

              Date latestReadingTime = new Date(entry.getDate());
              model.addAttribute("latestReadingTime", latestReadingTime);
              model.addAttribute("latestReading", String.format("%,.1f", entry.getSgvInMmol()));

              List<DeviceStatus> deviceStatuses =
                  deviceStatusRepository.findTop2ByOrderByCreatedAtDesc();
              if (deviceStatuses.size() == 2
                  && isInTheSameDay(deviceStatuses.get(0).getCreatedAt(), entry.getSysTime())) {
                model.addAttribute("device1", deviceStatuses.get(0));
                model.addAttribute("device2", deviceStatuses.get(1));
              }
            });

    return "barGraph";
  }

  public boolean isInTheSameDay(LocalDateTime localDtTm1, LocalDateTime localDtTm2) {
    return localDtTm1.getYear() == localDtTm2.getYear()
        && localDtTm1.getMonth() == localDtTm2.getMonth()
        && localDtTm1.getDayOfMonth() == localDtTm2.getDayOfMonth();
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
