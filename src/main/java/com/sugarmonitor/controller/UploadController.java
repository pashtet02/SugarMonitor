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
  public @ResponseBody ResponseEntity<Void> uploadDataFromPhone(@RequestBody Entry entry) {
    System.out.println("\n\n\n\nENTRY:::" + entry);
    Entry save = entryRepository.save(entry);
    System.out.println("\n\n\n\nENTRY after save:::" + save);
    return ResponseEntity.ok().build();
  }
}
