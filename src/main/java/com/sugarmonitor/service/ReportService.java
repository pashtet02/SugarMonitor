package com.sugarmonitor.service;

import com.sugarmonitor.dto.LineGraphDTO;
import com.sugarmonitor.dto.Report;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import java.util.List;
import java.util.Map;

public interface ReportService {
  String generateReportName(List<Entry> entries);

  Report createReport(List<Entry> entries, Profile userProfile, String reportName);

  double calculateHbA1c(double averageSgv);

  double calculateStandardDeviation(List<Entry> entries, Profile activeProfile);

  List<LineGraphDTO> groupEntriesByWeeks(List<Entry> entries, Profile activeProfile);

  Map<String, Map<String, Double>> groupEntriesByDay(List<Entry> entries, Profile activeProfile);
}
