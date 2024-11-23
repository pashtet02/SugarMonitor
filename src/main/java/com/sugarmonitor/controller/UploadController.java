package com.sugarmonitor.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sugarmonitor.model.DeviceStatus;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.DeviceStatusRepository;
import com.sugarmonitor.repos.EntryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
@RequiredArgsConstructor
@Slf4j
public class UploadController {

  private final ObjectMapper objectMapper;
  private final EntryRepository entryRepository;

  private final DeviceStatusRepository deviceStatusRepository;

  @PostMapping("/upload")
  public @ResponseBody ResponseEntity<String> uploadEntries(@RequestBody String requestJSON) {
    List<Entry> entries = new ArrayList<>();
    try {

      entries = objectMapper.readValue(requestJSON, new TypeReference<>() {});
      log.info("Received entries: {}", entries.size());

      List<Entry> savedEntries = entryRepository.saveAll(entries);
      log.info("Stored entries: {}", savedEntries.size());
      for (Entry e : entries) {
        log.debug("Uploaded entry: {}", e);
      }
    } catch (Exception e) {
      log.error("uploadEntries() Request {}, error message: {}", requestJSON, e.getMessage());
      ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("FAILURE " + e.getMessage() + " while uploading entries");
    }
    return ResponseEntity.ok("Uploaded " + entries.size() + " entries");
  }

  @PostMapping("/upload/devicestatus")
  public @ResponseBody ResponseEntity<String> uploadDeviceData(@RequestBody String requestJSON) {
    List<DeviceStatus> deviceStatuses = new ArrayList<>();
    try {
      deviceStatuses = objectMapper.readValue(requestJSON, new TypeReference<>() {});
      log.info("Received statuses: {}", deviceStatuses);
      for (DeviceStatus status : deviceStatuses) {
        log.debug("Uploaded status: {}", status);
      }
      List<DeviceStatus> savedEntries = deviceStatusRepository.saveAll(deviceStatuses);
      log.info("Stored status: {}", savedEntries.size());
    } catch (Exception e) {
      log.error("uploadDeviceData() Request {}, error message: {}", requestJSON, e.getMessage());
      ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("FAILURE " + e.getMessage() + " while uploading device status");
    }
    return ResponseEntity.ok("Uploaded " + deviceStatuses.size() + " statuses");
  }
}
