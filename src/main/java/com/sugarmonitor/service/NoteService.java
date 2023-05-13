package com.sugarmonitor.service;

import com.sugarmonitor.model.Note;
import java.time.LocalDateTime;
import java.util.List;

public interface NoteService {
  List<Note> getAllNotes();

  Note findById(String noteId);

  void deleteById(String noteId);

  void upsertNote(Note noteToBeCreated);

  void sortNotesByUpdatedAt(List<Note> notes);

  List<Note> findAllByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
