package com.sugarmonitor.controller;

import com.sugarmonitor.model.DeviceStatus;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.repos.DeviceStatusRepository;
import com.sugarmonitor.service.GraphService;
import com.sugarmonitor.service.ProfileService;
import java.time.Instant;
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

  private final GraphService graphService;

  private final ProfileService profileService;

  private final DeviceStatusRepository deviceStatusRepository;

  @GetMapping("/displayBarGraph")
  public String barGraph(
      @RequestParam(required = false, defaultValue = "2") Long displayForLast, Model model) {

    Map<String, Double> map = new LinkedHashMap<>();

    Profile activeProfile = profileService.getProfile();

    Date from = Date.from(Instant.now().minus(displayForLast, ChronoUnit.HOURS));
    Date to = Date.from(Instant.now());

    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());

    data.forEach(
        entry ->
            map.put(
                graphService.convertEntryDateIntoStringOnGraph(entry),
                entry.getSgv(activeProfile.getUnits())));

    model.addAttribute("sugarMap", map);
    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    // find two last Entry readings and get their diff in value to see trend (like +0.1 mmol,
    // -0.6mmol)
    if (data.size() >= 2) {
      Entry secondLastReading =
          data.stream()
              .sorted(Comparator.comparing(Entry::getDate))
              .collect(Collectors.toList())
              .get(data.size() - 2);

      data.stream()
          .max(Comparator.comparing(Entry::getDate))
          .ifPresent(
              entry -> {
                double differencePrevVsLatest =
                    entry.getSgv(activeProfile.getUnits())
                        - secondLastReading.getSgv(activeProfile.getUnits());
                model.addAttribute(
                    "lastReadingValue",
                    (differencePrevVsLatest < 0 ? "" : "+")
                        + String.format("%,.1f", differencePrevVsLatest));

                Date latestReadingTime = new Date(entry.getDate());
                model.addAttribute(
                    "titleText",
                    graphService.createTitle(entry, differencePrevVsLatest, activeProfile));
                model.addAttribute("latestReadingTime", latestReadingTime);
                model.addAttribute(
                    "latestReading",
                    String.format("%,.1f", entry.getSgv(activeProfile.getUnits())));

                List<DeviceStatus> deviceStatuses =
                    deviceStatusRepository.findTop2ByOrderByCreatedAtDesc();
                if (deviceStatuses.size() == 2
                    && graphService.isInTheSameDay(
                        deviceStatuses.get(0).getCreatedAt(), entry.getSysTime())) {
                  model.addAttribute("device1", deviceStatuses.get(0));
                  model.addAttribute("device2", deviceStatuses.get(1));
                }
              });
    }
    return "barGraph";
  }
}
