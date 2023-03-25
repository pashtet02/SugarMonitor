package com.sugarmonitor.repos;

import com.sugarmonitor.model.Entry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface EntryRepository extends MongoRepository<Entry, String> {

  @Query(value = "{dateString:'?0'}")
  List<Entry> findAllForDate(Date date);

}
