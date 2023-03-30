package com.sugarmonitor.repos;

import com.sugarmonitor.model.DeviceStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceStatusRepository extends MongoRepository<DeviceStatus, String> {

  List<DeviceStatus> findTop2ByOrderByCreatedAtDesc();

  DeviceStatus findTopByOrderByCreatedAtAsc();
}
