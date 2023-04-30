package com.sugarmonitor.repos;

import com.sugarmonitor.model.Entry;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends MongoRepository<Entry, String> {
  List<Entry> findByDateBetween(long dateGT, long dateLT);
}
