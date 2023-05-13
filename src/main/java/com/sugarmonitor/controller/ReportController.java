package com.sugarmonitor.controller;

import com.sugarmonitor.dto.LineGraphDTO;
import com.sugarmonitor.dto.Report;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.GraphService;
import com.sugarmonitor.service.ProfileService;
import com.sugarmonitor.service.ReportService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  private final ReportService reportService;

  @GetMapping()
  public String returnReportPage(@AuthenticationPrincipal User user, Model model) {
    model.addAttribute("dayToDayTabActive", true);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", false);
    model.addAttribute("user", user);

    return "report";
  }

  @GetMapping("/daytoday")
  public String generateDayToDayReports(
      @AuthenticationPrincipal User user,
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
    switch (generateFor) {
      case "today":
        from = fromCal.getTime();
        to = new Date();
        break;
      case "2days":
        fromCal.add(Calendar.DATE, -1);
        from = fromCal.getTime();
        to = new Date();
        break;
      case "week":
        fromCal.add(Calendar.DATE, -7);
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      case "month":
        fromCal.add(Calendar.MONTH, -1);
        fromCal.getTime();
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      case "3months":
        fromCal.add(Calendar.MONTH, -3);
        fromCal.getTime();
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      default:
        if (fromDate == null) {
          // set default value as 7 days before current time
          fromCal.add(Calendar.DATE, -7);
          from = fromCal.getTime();
        } else {
          from = fromDate;
        }

        if (toDate == null) {
          to = toCal.getTime();
        } else {
          toCal.setTime(toDate);
          toCal.set(Calendar.HOUR_OF_DAY, 23);
          toCal.set(Calendar.MINUTE, 59);
          toCal.set(Calendar.SECOND, 59);
          to = toCal.getTime();
        }
        break;
    }

    Profile activeProfile = profileService.getProfile();
    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());
    Map<String, Map<String, Double>> map = reportService.groupEntriesByDay(data, activeProfile);

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", true);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", false);
    model.addAttribute("map", map);
    model.addAttribute("user", user);
    return "report";
  }

  @GetMapping("/weektoweek")
  public String generateWeekToWeekReports(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "generateFor", required = false, defaultValue = "") String generateFor,
      @RequestParam(name = "fromDateWeek", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDateWeek,
      @RequestParam(name = "toDateWeek", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date toDateWeek,
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
    switch (generateFor) {
      case "week":
        // Set the day of the week to Monday
        fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // If the resulting date is after today's date, subtract 7 days
        if (fromCal.getTime().after(new Date())) {
          fromCal.add(Calendar.DAY_OF_MONTH, -7);
        }
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      case "2weeks":
        // Set the day of the week to Monday
        fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fromCal.add(Calendar.DAY_OF_MONTH, -7);
        // If the resulting date is after today's date, subtract 14 days
        if (fromCal.getTime().after(new Date())) {
          fromCal.add(Calendar.DAY_OF_MONTH, -7);
        }
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      case "month":
        // Set the day of the week to Monday
        fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fromCal.add(Calendar.DAY_OF_MONTH, -21);
        // If the resulting date is after today's date, subtract 14 days
        if (fromCal.getTime().after(new Date())) {
          fromCal.add(Calendar.DAY_OF_MONTH, -7);
        }
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      case "3months":
        // Set the day of the week to Monday
        fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fromCal.add(Calendar.DAY_OF_MONTH, -7 * 12);
        // If the resulting date is after today's date, subtract 13 weeks
        if (fromCal.getTime().after(new Date())) {
          fromCal.add(Calendar.DAY_OF_MONTH, -7);
        }
        from = fromCal.getTime();
        to = toCal.getTime();
        break;
      default:
        if (fromDateWeek == null) {
          // Set the day of the week to Monday
          fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

          // If the resulting date is after today's date, subtract 7 days
          if (fromCal.getTime().after(new Date())) {
            fromCal.add(Calendar.DAY_OF_MONTH, -7);
          }
          from = fromCal.getTime();
        } else {
          fromCal.setTime(fromDateWeek);
          fromCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
          from = fromCal.getTime();
        }

        if (toDateWeek == null) {
          to = toCal.getTime();
        } else {
          toCal.setTime(toDateWeek);
          to = toCal.getTime();
        }
        break;
    }

    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());
    List<LineGraphDTO> dtos = reportService.groupEntriesByWeeks(data, profileService.getProfile());

    Profile activeProfile = profileService.getProfile();

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", false);
    model.addAttribute("weekToWeekTabActive", true);
    model.addAttribute("generalTabActive", false);
    model.addAttribute("dtos", dtos);
    model.addAttribute("user", user);
    return "report";
  }

  @GetMapping("/general")
  public String generateGeneralReport(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "generateFor", required = false, defaultValue = "") String generateFor,
      @RequestParam(name = "fromDateGeneral", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDate,
      @RequestParam(name = "toDateGeneral", required = false)
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
    } else if (generateFor.equals("3months")) {
      fromCal.add(Calendar.MONTH, -3);
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
        to = toCal.getTime();
      } else to = toCal.getTime();
    }

    Profile activeProfile = profileService.getProfile();
    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());

    String reportName = reportService.generateReportName(data);
    Report report = reportService.createReport(data, activeProfile, reportName);

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());

    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", false);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", true);
    model.addAttribute("user", user);
    model.addAttribute("report", report);
    return "report";
  }
}
