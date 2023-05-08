package com.sugarmonitor.controller;

import com.sugarmonitor.model.Note;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.NoteService;
import com.sugarmonitor.service.ProfileService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  public String getNotesList(@AuthenticationPrincipal User userInSession, Model model) {

    model.addAttribute("profile", profileService.getProfile());
    model.addAttribute("user", userInSession);
    model.addAttribute("notes", noteService.getAllNotes());
    model.addAttribute("noteToBeCreated", new Note());
    model.addAttribute("showNoteCreationForm", false);

    return "notesList";
  }

  @PostMapping("/add")
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
  public String updateNote(
      @AuthenticationPrincipal User userInSession, @PathVariable String noteId, Model model) {
    Note note = noteService.findById(noteId);
    model.addAttribute("noteToBeCreated", note);
    model.addAttribute("user", userInSession);
    model.addAttribute("profile", profileService.getProfile());
    model.addAttribute("showNoteCreationForm", true);

    return "notesList";
  }

  @GetMapping("/delete/{noteId}")
  public String deleteNode(
      @AuthenticationPrincipal User userInSession, @PathVariable String noteId, Model model) {
    noteService.deleteById(noteId);
    return "redirect:/notes";
  }
}
