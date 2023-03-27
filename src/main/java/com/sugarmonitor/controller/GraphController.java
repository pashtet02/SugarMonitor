package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GraphController {

  private final EntryRepository entryRepository;

  @GetMapping("/displayBarGraph")
  public String barGraph(
      @RequestParam(required = false, defaultValue = "2") Long displayForLast, Model model) {
    Map<String, Long> map = new LinkedHashMap<>();

    Date from = Date.from(Instant.now().minus(displayForLast, ChronoUnit.HOURS));
    Date to = Date.from(Instant.now());

    List<Entry> all = entryRepository.findByDateBetween(from.getTime(), to.getTime());
    all.forEach(entry -> map.put(entry.getDateString(), entry.getSgv() / 18));
    model.addAttribute("sugarMap", map);
    return "barGraph";
  }
}
