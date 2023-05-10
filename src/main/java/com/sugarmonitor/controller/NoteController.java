package com.sugarmonitor.controller;

import com.sugarmonitor.model.Note;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.NoteService;
import com.sugarmonitor.service.ProfileService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {
  private final NoteService noteService;

  private final ProfileService profileService;

  @GetMapping
  public String getNotesList(
      @AuthenticationPrincipal User userInSession,
      @RequestParam(name = "findFor", required = false, defaultValue = "week") String findFor,
      @RequestParam(name = "fromDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date fromDateParam,
      @RequestParam(name = "toDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date toDateParam,
      Model model) {
    List<Note> requestedNotes = null;
    if (fromDateParam != null && toDateParam != null) {
      // convert the Date to a LocalDateTime
      LocalDateTime fromDate =
          fromDateParam.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      LocalDateTime toDate =
          toDateParam.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      requestedNotes = noteService.findAllByCreatedAtBetween(fromDate, toDate);
    } else if (fromDateParam != null) {
      LocalDateTime fromDate =
          fromDateParam.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      requestedNotes = noteService.findAllByCreatedAtBetween(fromDate, LocalDateTime.now());
    }
    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
    switch (findFor) {
      case "today":
        requestedNotes = noteService.findAllByCreatedAtBetween(startOfDay, LocalDateTime.now());
        break;
      case "2days":
        requestedNotes =
            noteService.findAllByCreatedAtBetween(startOfDay.minusHours(48), LocalDateTime.now());
        break;
      case "week":
        requestedNotes =
            noteService.findAllByCreatedAtBetween(startOfDay.minusWeeks(1), LocalDateTime.now());
        break;
      case "month":
        requestedNotes =
            noteService.findAllByCreatedAtBetween(startOfDay.minusMonths(1), LocalDateTime.now());
        break;
    }

    model.addAttribute("notes", requestedNotes);

    model.addAttribute("profile", profileService.getProfile());
    model.addAttribute("user", userInSession);
    model.addAttribute("noteToBeCreated", Note.builder().mealType("OTHER").build());
    model.addAttribute("showNoteCreationForm", false);

    return "notesList";
  }

  @PostMapping("/add")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String createNote(
      @AuthenticationPrincipal User userInSession,
      @ModelAttribute("noteToBeCreated") @Valid Note noteToBeCreated,
      BindingResult bindingResult,
      Model model) {
    model.addAttribute("user", userInSession);
    model.addAttribute("profile", profileService.getProfile());

    if (bindingResult.hasErrors()) {
      model.addAttribute("showNoteCreationForm", true);
      return "notesList";
    }

    noteService.upsertNote(noteToBeCreated);

    return "redirect:/notes";
  }

  // When edit is pressed form will be filled with requested Note
  @GetMapping("/update/{noteId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String updateNote(
      @AuthenticationPrincipal User userInSession, @PathVariable String noteId, Model model) {
    Note note = noteService.findById(noteId);
    model.addAttribute("noteToBeCreated", note);
    model.addAttribute("user", userInSession);
    model.addAttribute("profile", profileService.getProfile());
    model.addAttribute("showNoteCreationForm", true);

    return "notesList";
  }

  /*  @PostMapping("/update/{}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String createNote(
          @AuthenticationPrincipal User userInSession,
          @ModelAttribute("noteToBeCreated") @Valid Note noteToBeCreated,
          BindingResult bindingResult,
          Model model) {
    model.addAttribute("user", userInSession);
    model.addAttribute("profile", profileService.getProfile());

    if (bindingResult.hasErrors()) {
      model.addAttribute("showNoteCreationForm", true);
      return "notesList";
    }

    noteService.upsertNote(noteToBeCreated);

    return "redirect:/notes";
  }*/

  @GetMapping("/delete/{noteId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String deleteNode(
      @AuthenticationPrincipal User userInSession, @PathVariable String noteId, Model model) {
    noteService.deleteById(noteId);
    return "redirect:/notes";
  }
}
