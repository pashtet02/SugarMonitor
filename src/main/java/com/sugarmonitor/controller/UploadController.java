package com.sugarmonitor.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sugarmonitor.model.DeviceStatus;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.DeviceStatusRepository;
import com.sugarmonitor.repos.EntryRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequiredArgsConstructor
public class UploadController {

  private final ObjectMapper objectMapper;
  private final EntryRepository entryRepository;

  private final DeviceStatusRepository deviceStatusRepository;

  @PostMapping("/upload")
  public @ResponseBody ResponseEntity<Void> uploadEntries(@RequestBody String requestJSON) {
    Collection<Entry> entries = null;
    try {
      entries = new ObjectMapper().readValue(requestJSON, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    System.out.println("\n\n\n\nENTRY list:::" + entries);
    entries.forEach(System.out::println);
    List<Entry> savedEntries = entryRepository.saveAll(entries);
    System.out.println("\n\n\n\nENTRY list saved:::" + savedEntries);
    savedEntries.forEach(System.out::println);
    System.out.println();
    return ResponseEntity.ok().build();
  }

  @PostMapping("/upload/devicestatus")
  public @ResponseBody ResponseEntity<Void> uploadDeviceData(@RequestBody String requestJSON) {
    DeviceStatus deviceStatus = null;
    try {
      deviceStatus = objectMapper.readValue(requestJSON, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    deviceStatus.setCreatedAt(new Date().toString());
    System.out.println("\n\n\n\nDEVICE STATUSES list:::" + deviceStatus);
    DeviceStatus savedEntries = deviceStatusRepository.save(deviceStatus);
    System.out.println("\n\n\n\nDEVICE STATUSES saved:::" + savedEntries);
    // savedEntries.forEach(System.out::println);
    System.out.println();
    return ResponseEntity.ok().build();
  }
}
