package com.sugarmonitor.service;

import com.sugarmonitor.model.Note;
import com.sugarmonitor.repos.NoteRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class NoteService {

  private final NoteRepository noteRepository;

  public List<Note> getAllNotes() {
    return noteRepository.findAll();
  }

  public Note findById(String noteId) {
    return noteRepository
        .findById(noteId)
        .orElseThrow(() -> new RuntimeException("Note not found!"));
  }

  public void deleteById(String noteId) {
    noteRepository.deleteById(noteId);
  }

  public void upsertNote(Note noteToBeCreated) {
    noteToBeCreated.setUpdatedAt(new Date());
    noteRepository.save(noteToBeCreated);
  }

  public void sortNotesByUpdatedAt(List<Note> notes) {
    notes.sort(
        (note1, note2) -> {
          Date updatedAt1 = note1.getUpdatedAt();
          Date updatedAt2 = note2.getUpdatedAt();
          return updatedAt2.compareTo(updatedAt1);
        });
  }

  public List<Note> findAllByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate) {
    return noteRepository.findAllByUpdatedAtBetween(fromDate, toDate);
  }
}
