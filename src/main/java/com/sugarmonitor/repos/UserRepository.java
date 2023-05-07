package com.sugarmonitor.repos;

import com.sugarmonitor.model.Role;
import com.sugarmonitor.model.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Long> {
  User findByUsername(String username);

  List<User> findByRolesNotIn(Set<Role> roles);
}
