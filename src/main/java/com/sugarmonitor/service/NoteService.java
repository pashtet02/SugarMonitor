package com.sugarmonitor.service;

import com.sugarmonitor.model.Note;
import com.sugarmonitor.repos.NoteRepository;
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
    if (noteToBeCreated.getId() == null) {
      noteToBeCreated.setCreatedAt(new Date());
    }
    noteToBeCreated.setUpdatedAt(new Date());
    noteRepository.save(noteToBeCreated);
  }
}
