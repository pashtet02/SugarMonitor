package com.sugarmonitor.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LineGraphDTO {

  private String weekTitle;

  private List<Map<String, Double>> weekEntries;

  public Map<String, Double> getMondayEntries() {
    return weekEntries.get(0);
  }

  public Map<String, Double> getTuesdayEntries() {
    return weekEntries.get(1);
  }

  public Map<String, Double> getWednesdayEntries() {
    return weekEntries.get(2);
  }

  public Map<String, Double> getThursdayEntries() {
    return weekEntries.get(3);
  }

  public Map<String, Double> getFridayEntries() {
    return weekEntries.get(4);
  }

  public Map<String, Double> getSaturdayEntries() {
    return weekEntries.get(5);
  }

  public Map<String, Double> getSundayEntries() {
    return weekEntries.get(6);
  }
}
