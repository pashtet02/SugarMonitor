package com.sugarmonitor.service;

import com.sugarmonitor.model.Entry;
import java.time.LocalDateTime;
import java.util.List;

public interface GraphService {
  List<Entry> findByDateBetween(long from, long to);

  boolean isInTheSameDay(LocalDateTime localDtTm1, LocalDateTime localDtTm2);

  String createTitle(Entry entry, double differencePrevVsLatest);

  String parseUnicodeChar(String unicodeString);

  String convertEntryDateIntoStringOnGraph(Entry entry);
}
