package com.sugarmonitor.service.impl;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.model.Profile;
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
  public String createTitle(Entry entry, double differencePrevVsLatest, Profile activeProfile) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%,.1f", entry.getSgv(activeProfile.getUnits())));
    sb.append(" ");
    double multiplier = activeProfile.getUnits().equals("mmol") ? (double) 1 : (double) 18;
    if (differencePrevVsLatest >= multiplier) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("21C8")); // 2 arrows UP
    } else if (differencePrevVsLatest >= 0.6 * multiplier) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F865")); // arrow UP
    } else if (differencePrevVsLatest > 0.3 * multiplier) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F865")); // arrow UP with 45-degree rotation
    } else if (differencePrevVsLatest <= 0.3 * multiplier
        && differencePrevVsLatest >= 0.0 * multiplier) {
      sb.append("+");
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F862")); // arrow strait
    } else if (differencePrevVsLatest < 0.0 * multiplier
        && differencePrevVsLatest >= -0.3 * multiplier) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F862")); // arrow strait
    } else if (differencePrevVsLatest < -0.3 * multiplier
        && differencePrevVsLatest >= -0.5 * multiplier) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F866")); // arrow DOWN with 45-degree rotation
    } else if (differencePrevVsLatest <= -0.6 * multiplier
        && differencePrevVsLatest > -1.0 * multiplier) {
      sb.append(String.format("%,.1f", differencePrevVsLatest));
      sb.append(" ");
      sb.append(parseUnicodeChar("1F863")); // arrow DOWN
    } else if (differencePrevVsLatest <= -1.0 * multiplier) { // U+
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
    LocalDateTime entrySysTime = entry.getSysTime();
    if (entrySysTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
      dayOfMonth =
          entrySysTime.getDayOfMonth() + " " + entrySysTime.getMonth().toString().substring(0, 3);
    }

    int hour = entrySysTime.getHour();
    // To avoid time like 2:1 or 14:6 or 2:55
    String hourStr = String.valueOf(hour);
    if (hour < 10) {
      hourStr = "0" + hourStr;
    }
    int minute = entrySysTime.getMinute();
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

  @Override
  public String convertEntryHourIntoStringOnGraph(Entry entry) {

    LocalDateTime entrySysTime = entry.getSysTime();

    int hour = entrySysTime.getHour();
    // To avoid time like 2:1 or 14:6 or 2:55
    String hourStr = String.valueOf(hour);
    if (hour < 10) {
      hourStr = "0" + hourStr;
    }
    int minute = entrySysTime.getMinute();
    // To avoid time like 2:1 or 14:6 or 2:55
    String minuteStr = String.valueOf(minute);
    if (minute < 10) {
      minuteStr = "0" + minuteStr;
    }

    return hourStr + ":" + minuteStr;
  }
}
