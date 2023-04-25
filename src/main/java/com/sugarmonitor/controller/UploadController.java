package com.sugarmonitor.controller;

import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.EntryRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequiredArgsConstructor
public class UploadController {

  private final EntryRepository entryRepository;

  @PostMapping("/upload")
  public @ResponseBody ResponseEntity<Void> uploadDataFromPhone(@RequestBody List<Entry> entries) {
    System.out.println("\n\n\n\nENTRY list:::" + entries);
    entries.forEach(System.out::println);
    List<Entry> savedEntries = entryRepository.saveAll(entries);
    System.out.println("\n\n\n\nENTRY list saved:::" + savedEntries);
    savedEntries.forEach(System.out::println);
    return ResponseEntity.ok().build();
  }
}
