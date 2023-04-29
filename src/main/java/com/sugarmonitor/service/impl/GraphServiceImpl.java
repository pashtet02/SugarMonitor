package com.sugarmonitor.service.impl;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import com.sugarmonitor.service.GraphService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class GraphServiceImpl implements GraphService {
  private final EntryRepository entryRepository;

  @Override
  public List<Entry> findByDateBetween(long from, long to) {
    return entryRepository.findByDateBetween(from, to);
  }

  @Override
  public boolean isInTheSameDay(LocalDateTime localDtTm1, LocalDateTime localDtTm2) {
    return localDtTm1.getYear() == localDtTm2.getYear()
        && localDtTm1.getMonth() == localDtTm2.getMonth()
        && localDtTm1.getDayOfMonth() == localDtTm2.getDayOfMonth();
  }

  @Override
  public String createTitle(Entry entry, double differencePrevVsLatest) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%,.1f", entry.getSgvInMmol()));
    sb.append(" ");

    if (differencePrevVsLatest >= 1.0) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("21C8")); // 2 arrows UP
    } else if (differencePrevVsLatest < 1.0 && differencePrevVsLatest >= 0.6) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F865")); // arrow UP
    } else if (differencePrevVsLatest > 0.3) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F865")); // arrow UP with 45-degree rotation
    } else if (differencePrevVsLatest <= 0.3 && differencePrevVsLatest >= 0.0) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F862")); // arrow strait
    } else if (differencePrevVsLatest < 0.0 && differencePrevVsLatest >= -0.3) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F862")); // arrow strait
    } else if (differencePrevVsLatest < -0.3 && differencePrevVsLatest >= -0.5) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F866")); // arrow DOWN with 45-degree rotation
    } else if (differencePrevVsLatest <= -0.6 && differencePrevVsLatest > -1.0) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F863")); // arrow DOWN
    } else if (differencePrevVsLatest <= -1.0) { // U+
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("21CA")); // 2 arrows DOWN
    }

    return sb.toString();
  }

  @Override
  public String parseUnicodeChar(String unicodeString) {
    int codepoint = Integer.parseInt(unicodeString, 16);
    return new String(Character.toChars(codepoint));
  }

  @Override
  public String convertEntryDateIntoStringOnGraph(Entry entry) {

    String dayOfMonth = null;
    if (entry.getSysTime().getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
      dayOfMonth =
          entry.getSysTime().getDayOfMonth()
              + " "
              + entry.getSysTime().getMonth().toString().substring(0, 3);
    }

    int hour = entry.getSysTime().getHour();
    // To avoid time like 2:1 or 14:6 or 2:55
    String hourStr = String.valueOf(hour);
    if (hour < 10) {
      hourStr = "0" + hourStr;
    }
    int minute = entry.getSysTime().getMinute();
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
