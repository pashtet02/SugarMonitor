package com.sugarmonitor.repos;

import com.sugarmonitor.model.Note;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
  List<Note> findAllByUpdatedAtBetween(LocalDateTime createdAt, LocalDateTime createdAt2);
}
