package com.sugarmonitor.service.impl;

import com.sugarmonitor.model.Profile;
import com.sugarmonitor.repos.ProfileRepository;
import com.sugarmonitor.service.ProfileService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {
  private static Profile activeProfile;

  private final ProfileRepository profileRepository;

  @PostConstruct
  public void postConstruct() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String formattedDateTime = currentDateTime.format(formatter);
    activeProfile =
        profileRepository
            .findFirstByOrderByCreatedAtDesc()
            .orElse(
                Profile.builder()
                    .language("EN")
                    .createdAt(formattedDateTime)
                    .timeFormat(24) // enum here too
                    .highAlertSoundEnabled(true)
                    .highBoundLimit(9.9)
                    .lowAlertSoundEnabled(true)
                    .lowerBoundLimit(3.9)
                    .units("mmol") // mmol or mgdl replase here with ENUM later
                    .build());
    profileRepository.save(activeProfile);
  }

  @Override
  public double getLowerBoundLimit() {
    return activeProfile.getLowerBoundLimit();
  }

  @Override
  public double getHighBoundLimit() {
    return activeProfile.getHighBoundLimit();
  }

  @Override
  public Profile getProfile() {
    return activeProfile;
  }

  @Override
  public Profile save(Profile newProfile) {
    this.activeProfile = profileRepository.save(newProfile);
    return activeProfile;
  }

  @Override
  public int getYAxisGraphMaxLimit() {
    if (activeProfile.getUnits().equals("mmol")) {
      return 20;
    } else {
      return 20 * 18;
    }
  }

  @Override
  public int getYAxisGraphMinLimit() {
    if (activeProfile.getUnits().equals("mmol")) {
      return 2;
    } else {
      return 2 * 18;
    }
  }

  @Override
  public int getYAxisGraphStep() {
    if (activeProfile.getUnits().equals("mmol")) {
      return 2;
    } else {
      return 2 * 18;
    }
  }
}
