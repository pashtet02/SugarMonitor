package com.sugarmonitor.service.impl;

import static java.lang.Double.parseDouble;

import com.sugarmonitor.dto.LineGraphDTO;
import com.sugarmonitor.dto.Report;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import com.sugarmonitor.service.GraphService;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements com.sugarmonitor.service.ReportService {

  private final GraphService graphService;

  @Override
  public String generateReportName(List<Entry> entries) {
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

  @Override
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
            String.format("%.1f", ((double) lowSugarEntries.size() / totalEntries) * 100.0)
                .replace(",", "."));
    double highSugarPercentage =
        parseDouble(
            String.format("%.1f", ((double) highSugarEntries.size() / totalEntries) * 100.0)
                .replace(",", "."));
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
        .inRangeSugarPercentage(
            parseDouble(
                String.format("%.1f", (100.0 - lowSugarPercentage - highSugarPercentage))
                    .replace(",", ".")))
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
  @Override
  public double calculateHbA1c(double averageSgv) {
    return parseDouble(String.format("%.1f", (averageSgv + 1.59) / 1.819).replace(",", "."));
  }

  private double calculateAverageSgv(List<Entry> entries, Profile activeProfile) {
    double result =
        entries.stream().mapToDouble(e -> e.getSgv(activeProfile.getUnits())).average().orElse(0.0);
    return parseDouble(String.format("%.1f", result).replace(",", "."));
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
    return parseDouble(String.format("%.1f", median).replace(",", "."));
  }

  @Override
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
    return parseDouble(String.format("%.1f", standardDeviation).replace(",", "."));
  }

  @Override
  public List<LineGraphDTO> groupEntriesByWeeks(List<Entry> entries, Profile activeProfile) {
    List<LineGraphDTO> dtos = new LinkedList<>();
    Map<String, List<Map<String, Double>>> result = new LinkedHashMap<>();
    for (Entry entry : entries) {
      String day = entry.getDateString().replace("T", " ").split(" ")[0];

      LocalDate entryWrittenDate = LocalDate.parse(day);
      LocalDate mondayOfWeek =
          entryWrittenDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate sundayOfWeek =
          entryWrittenDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      String week =
          mondayOfWeek.format(DateTimeFormatter.ofPattern("dd-MM-yy"))
              + " - "
              + sundayOfWeek.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
      result
          .computeIfAbsent(
              week,
              k -> {
                LinkedList<Map<String, Double>> emptyList = new LinkedList<>();
                for (int i = 0; i < 7; i++) {
                  emptyList.add(new LinkedHashMap<>());
                }
                dtos.add(LineGraphDTO.builder().weekTitle(week).weekEntries(emptyList).build());
                return emptyList;
              })
          .get(entryWrittenDate.getDayOfWeek().getValue() - 1)
          .put(
              graphService.convertEntryHourIntoStringOnGraph(entry),
              entry.getSgv(activeProfile.getUnits()));
    }
    return dtos;
  }

  @Override
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
