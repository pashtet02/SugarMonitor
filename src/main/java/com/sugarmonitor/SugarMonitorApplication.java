package com.sugarmonitor;

import com.sugarmonitor.model.Role;
import com.sugarmonitor.model.User;
import com.sugarmonitor.repos.UserRepository;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SugarMonitorApplication implements CommandLineRunner {

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public static void main(String[] args) {
    SpringApplication.run(SugarMonitorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.findByUsername("admin") == null) {
      // Create user with username "admin", password "admin", and roles "USER" and "ADMIN"
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword(passwordEncoder.encode("admin"));
      admin.setRoles(new HashSet<>(Arrays.asList(Role.USER, Role.ADMIN)));
      userRepository.save(admin);
    }
    if (userRepository.findByUsername("pashtet") == null) {
      User admin = new User();
      admin.setUsername("pashtet");
      admin.setPassword(passwordEncoder.encode("pashtet"));
      admin.setRoles(new HashSet<>(Arrays.asList(Role.USER)));
      userRepository.save(admin);
    }
  }
}
