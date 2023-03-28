package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequiredArgsConstructor
public class DocumentController {

  private final EntryRepository entryRepository;

  @GetMapping("/entries")
  public List<Entry> getAllEntry() {
    List<Entry> all = entryRepository.findAll();
    return all.stream().limit(50).collect(Collectors.toList());
  }

  @GetMapping("/entries/{entryId}")
  public Entry getEntryById(@PathVariable final String entryId) {
    return entryRepository.findById(entryId).orElseGet(Entry::new);
  }
}
