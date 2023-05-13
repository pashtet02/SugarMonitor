package com.sugarmonitor.service.impl;

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
public class NoteServiceImpl implements com.sugarmonitor.service.NoteService {

  private final NoteRepository noteRepository;

  @Override
  public List<Note> getAllNotes() {
    return noteRepository.findAll();
  }

  @Override
  public Note findById(String noteId) {
    return noteRepository
        .findById(noteId)
        .orElseThrow(() -> new RuntimeException("Note not found!"));
  }

  @Override
  public void deleteById(String noteId) {
    noteRepository.deleteById(noteId);
  }

  @Override
  public void upsertNote(Note noteToBeCreated) {
    noteToBeCreated.setUpdatedAt(new Date());
    Note save = noteRepository.save(noteToBeCreated);
    return;
  }

  @Override
  public void sortNotesByUpdatedAt(List<Note> notes) {
    notes.sort(
        (note1, note2) -> {
          Date updatedAt1 = note1.getUpdatedAt();
          Date updatedAt2 = note2.getUpdatedAt();
          return updatedAt2.compareTo(updatedAt1);
        });
  }

  @Override
  public List<Note> findAllByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate) {
    return noteRepository.findAllByUpdatedAtBetween(fromDate, toDate);
  }
}
