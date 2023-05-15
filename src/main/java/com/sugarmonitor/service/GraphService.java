package com.sugarmonitor.service;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
import java.time.LocalDateTime;
import java.util.List;

public interface GraphService {
  List<Entry> findByDateBetween(long from, long to);

  boolean isInTheSameDay(LocalDateTime localDtTm1, LocalDateTime localDtTm2);

  long getTimeSpendOfLastReading(long entryDate);

  String createTitle(Entry entry, double differencePrevVsLatest, Profile activeProfile);

  String parseUnicodeChar(String unicodeString);

  String convertEntryDateIntoStringOnGraph(Entry entry);

  String convertEntryHourIntoStringOnGraph(Entry entry);
}
