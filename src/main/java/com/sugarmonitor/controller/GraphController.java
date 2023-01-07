package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class GraphController {

  private final EntryRepository entryRepository;


  @GetMapping("/displayBarGraph")
  public String barGraph(Model model) {
    Map<String, Long> map = new LinkedHashMap<>();
    List<Entry> all = entryRepository.findAll();
    all.stream().limit(50).forEach(entry -> map.put(entry.getDateString(), entry.getSgv() / 18));
    model.addAttribute("sugarMap", map);
    return "barGraph";
  }

}
