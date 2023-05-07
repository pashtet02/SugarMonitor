package com.sugarmonitor.service.impl;

import com.sugarmonitor.model.Role;
import com.sugarmonitor.model.User;
import com.sugarmonitor.repos.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username.isBlank()) {
      throw new RuntimeException("Invalid username provided! Cannot be null, empty or blank!");
    }

    return userRepository.findByUsername(username);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public boolean addUser(User user, Role role) {
    User userFromDb = userRepository.findByUsername(user.getUsername());

    if (userFromDb != null) {
      return false;
    }

    user.setRoles(Collections.singleton(role));
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    userRepository.save(user);
    return true;
  }
}
