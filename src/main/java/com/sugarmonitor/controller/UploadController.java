package com.sugarmonitor.controller;

import com.sugarmonitor.model.DeviceStatus;
import com.sugarmonitor.model.Entry;
import com.sugarmonitor.repos.DeviceStatusRepository;
import com.sugarmonitor.repos.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
