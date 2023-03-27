package com.sugarmonitor.repos;

import com.sugarmonitor.model.Entry;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryRepository extends MongoRepository<Entry, String> {
  List<Entry> findByDateBetween(long dateGT, long dateLT);
}
