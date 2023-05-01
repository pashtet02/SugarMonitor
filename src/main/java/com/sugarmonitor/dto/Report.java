package com.sugarmonitor.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class Report {

  private String name;

  private double lowSugarPercentage;
  private long numOfLowEntries;

  private double highSugarPercentage;
  private long numOfHighEntries;

  private double inRangeSugarPercentage;
  private long numOfInRangeEntries;

  private long totalEntries;

  private double averageLow;
  private double averageHigh;
  private double averageInRange;
  private double averageTotal;

  private double medianLow;
  private double medianHigh;
  private double medianInRange;
  private double medianInTotal;

  private double stdDevLow;
  private double stdDevHigh;
  private double stdDevInRange;
  private double stdDevTotal;

  private double HbA1cTotal;
}
