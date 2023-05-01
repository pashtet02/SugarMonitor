package com.sugarmonitor.controller;

import static java.lang.Double.parseDouble;

import com.sugarmonitor.dto.Report;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.service.GraphService;
import com.sugarmonitor.service.ProfileService;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
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
    model.addAttribute("dayToDayTabActive", true);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", false);

    return "report";
  }

  @GetMapping("/daytoday")
  public String generateDayToDayReports(
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
    Map<String, Map<String, Double>> map = groupEntriesByDay(data, activeProfile);

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", true);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", false);
    model.addAttribute("map", map);
    return "report";
  }

  @GetMapping("/weektoweek")
  public String generateWeekToWeekReports(
      @RequestParam(name = "generateFor", required = false, defaultValue = "") String generateFor,
      @RequestParam(name = "fromDateWeek", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDateWeek,
      @RequestParam(name = "toDateWeek", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date toDateWeek,
      Model model) {

    Calendar fromCal = Calendar.getInstance();
    fromCal.set(Calendar.HOUR_OF_DAY, 0);
    fromCal.set(Calendar.MINUTE, 0);
    fromCal.set(Calendar.SECOND, 0);

    Calendar toCal = Calendar.getInstance();
    toCal.set(Calendar.HOUR_OF_DAY, 23);
    toCal.set(Calendar.MINUTE, 59);
    toCal.set(Calendar.SECOND, 59);

    LocalDateTime monday = getStartOfPreviousMonday();

    Date from;
    Date to;
    fromCal.add(Calendar.DATE, -21);
    from = fromCal.getTime();
    to = toCal.getTime();

    List<Entry> data = graphService.findByDateBetween(from.getTime(), to.getTime());
    Map<String, List<Map<String, Double>>> map =
        groupEntriesByWeeks(data, profileService.getProfile());

    Profile activeProfile = profileService.getProfile();

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());
    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", false);
    model.addAttribute("weekToWeekTabActive", true);
    model.addAttribute("generalTabActive", false);
    model.addAttribute("weekMap", map);

    return "report";
  }

  @GetMapping("/general")
  public String generateGeneralReport(
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

    String reportName = generateReportName(data);
    Report report = createReport(data, activeProfile, reportName);

    model.addAttribute("lowSugarLine", profileService.getLowerBoundLimit());
    model.addAttribute("highSugarLine", profileService.getHighBoundLimit());
    model.addAttribute("yAxisGraphMinLimit", profileService.getYAxisGraphMinLimit());
    model.addAttribute("yAxisGraphMaxLimit", profileService.getYAxisGraphMaxLimit());
    model.addAttribute("yAxisGraphStep", profileService.getYAxisGraphStep());

    model.addAttribute("profile", activeProfile);
    model.addAttribute("dayToDayTabActive", false);
    model.addAttribute("weekToWeekTabActive", false);
    model.addAttribute("generalTabActive", true);

    model.addAttribute("report", report);
    return "report";
  }

  private String generateReportName(List<Entry> entries) {
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd.MM.yyyy");
    Date fromDate = new Date(entries.get(0).getDate());
    Date toDate = new Date(entries.get(entries.size() - 1).getDate());
    String dateRange = sdf.format(fromDate) + " - " + sdf.format(toDate);
    return "Glucose distribution ("
        + ChronoUnit.DAYS.between(fromDate.toInstant(), toDate.toInstant())
        + " days total, "
        + dateRange
        + ")";
  }

  public Report createReport(List<Entry> entries, Profile userProfile, String reportName) {
    long totalEntries = entries.size();
    List<Entry> lowSugarEntries =
        entries.stream()
            .filter(e -> e.getSgv(userProfile.getUnits()) < userProfile.getLowerBoundLimit())
            .collect(Collectors.toList());
    List<Entry> highSugarEntries =
        entries.stream()
            .filter(e -> e.getSgv(userProfile.getUnits()) > userProfile.getHighBoundLimit())
            .collect(Collectors.toList());
    List<Entry> inRangeEntries =
        entries.stream()
            .filter(
                e ->
                    (e.getSgv(userProfile.getUnits()) < userProfile.getHighBoundLimit())
                        && (e.getSgv(userProfile.getUnits()) > userProfile.getLowerBoundLimit()))
            .collect(Collectors.toList());

    double lowSugarPercentage =
        parseDouble(
            String.format("%.1f", ((double) lowSugarEntries.size() / totalEntries) * 100.0));
    double highSugarPercentage =
        parseDouble(
            String.format("%.1f", ((double) highSugarEntries.size() / totalEntries) * 100.0));
    double averageLowSGV = calculateAverageSgv(lowSugarEntries, userProfile);
    double averageInRangeSGV = calculateAverageSgv(inRangeEntries, userProfile);
    double averageHighSGV = calculateAverageSgv(highSugarEntries, userProfile);
    double averageTotalSGV = calculateAverageSgv(entries, userProfile);
    return Report.builder()
        .name(reportName)
        .lowSugarPercentage(lowSugarPercentage)
        .numOfLowEntries(lowSugarEntries.size())
        .averageLow(averageLowSGV)
        .medianLow(calculateMedianSgv(lowSugarEntries, userProfile))
        .stdDevLow(calculateStandardDeviation(lowSugarEntries, userProfile))
        .highSugarPercentage(highSugarPercentage)
        .numOfHighEntries(highSugarEntries.size())
        .averageHigh(averageHighSGV)
        .medianHigh(calculateMedianSgv(highSugarEntries, userProfile))
        .stdDevHigh(calculateStandardDeviation(highSugarEntries, userProfile))
        .inRangeSugarPercentage(100.0 - lowSugarPercentage - highSugarPercentage)
        .averageInRange(averageInRangeSGV)
        .medianInRange(calculateMedianSgv(inRangeEntries, userProfile))
        .averageTotal(averageTotalSGV)
        .stdDevInRange(calculateStandardDeviation(inRangeEntries, userProfile))
        .HbA1cTotal(calculateHbA1c(averageTotalSGV))
        .medianInTotal(calculateMedianSgv(entries, userProfile))
        .numOfInRangeEntries(totalEntries - lowSugarEntries.size() - highSugarEntries.size())
        .stdDevTotal(calculateStandardDeviation(entries, userProfile))
        .totalEntries(totalEntries)
        .build();
  }

  // TODO fix formulas are incorrect!
  public double calculateHbA1c(double averageSgv) {
    return parseDouble(String.format("%.1f", (averageSgv + 1.59) / 1.819));
  }

  private double calculateAverageSgv(List<Entry> entries, Profile activeProfile) {
    double result =
        entries.stream().mapToDouble(e -> e.getSgv(activeProfile.getUnits())).average().orElse(0.0);
    return parseDouble(String.format("%.1f", result));
  }

  private double calculateMedianSgv(List<Entry> entries, Profile activeProfile) {
    double median =
        entries.stream()
            .mapToDouble(e -> e.getSgv(activeProfile.getUnits()))
            .sorted()
            .toArray()[entries.size() / 2];
    if (entries.size() % 2 == 0) {
      median =
          (median
                  + entries.stream()
                      .mapToDouble(e -> e.getSgv(activeProfile.getUnits()))
                      .sorted()
                      .toArray()[(entries.size() / 2) - 1])
              / 2;
    }
    return parseDouble(String.format("%.1f", median));
  }

  public double calculateStandardDeviation(List<Entry> entries, Profile activeProfile) {
    // Calculate the mean
    double mean =
        entries.stream().mapToDouble(e -> e.getSgv(activeProfile.getUnits())).average().orElse(0.0);

    // Calculate the sum of squared differences from the mean
    double sumOfSquaredDifferences =
        entries.stream()
            .mapToDouble(e -> e.getSgv(activeProfile.getUnits()))
            .map(sgv -> Math.pow(sgv - mean, 2))
            .sum();

    // Calculate the variance
    double variance = sumOfSquaredDifferences / entries.size();

    // Calculate the standard deviation
    double standardDeviation = Math.sqrt(variance);

    // Round the result to one decimal place
    return parseDouble(String.format("%.1f", standardDeviation));
  }

  // Helper method to get the start of the previous Monday
  private static LocalDateTime getStartOfPreviousMonday() {
    return LocalDateTime.now()
        .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        .withHour(0)
        .withMinute(0)
        .withSecond(0);
  }

  public Map<String, List<Map<String, Double>>> groupEntriesByWeeks(
      List<Entry> entries, Profile activeProfile) {
    Map<String, List<Map<String, Double>>> result = new LinkedHashMap<>();
    for (Entry entry : entries) {
      String day = entry.getDateString().replace("T", " ").split(" ")[0];

      LocalDate entryWrittenDate = LocalDate.parse(day);
      LocalDate mondayOfWeek =
          entryWrittenDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate sundayOfWeek =
          entryWrittenDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      String week =
          mondayOfWeek.format(DateTimeFormatter.ofPattern("yy-MM-dd"))
              + ":"
              + sundayOfWeek.format(DateTimeFormatter.ofPattern("yy-MM-dd"));
      result
          .computeIfAbsent(
              week,
              k -> {
                LinkedList<Map<String, Double>> emptyList = new LinkedList<>();
                for (int i = 0; i < 7; i++) {
                  emptyList.add(new LinkedHashMap<>());
                }
                return emptyList;
              })
          .get(entryWrittenDate.getDayOfWeek().getValue() - 1)
          .put(
              graphService.convertEntryDateIntoStringOnGraph(entry),
              entry.getSgv(activeProfile.getUnits()));
    }
    return result;
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
