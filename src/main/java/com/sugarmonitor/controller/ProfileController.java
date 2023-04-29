package com.sugarmonitor.controller;

import com.sugarmonitor.model.Profile;
import com.sugarmonitor.repos.ProfileRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class ProfileController {
  private final ProfileRepository profileRepository;

  @GetMapping()
  public String mainReportMenu(Model model) {

    Profile userProfile = profileRepository.findFirstByOrderByCreatedAtDesc().get();

    model.addAttribute("preferableUnits", userProfile.getUnits());
    model.addAttribute("preferableTimeFormat", userProfile.getTimeFormat());
    model.addAttribute("enableHighAlertSound", userProfile.isHighAlertSoundEnabled());
    model.addAttribute("enableLowAlertSound", userProfile.isLowAlertSoundEnabled());
    model.addAttribute("highBoundNum", userProfile.getHighBoundLimit());
    model.addAttribute("lowerBoundNum", userProfile.getLowerBoundLimit());

    return "profile";
  }

  @PostMapping("/update")
  // LocalDateTime.parse("2023-04-29T20:14:03.523Z".replace("Z", "")
  public String updateProfile(
      @RequestParam(name = "units", required = false) String units,
      @RequestParam(name = "hours", required = false) int hoursFormat,
      @RequestParam(name = "highBoundNum", required = false) double highBoundNum,
      @RequestParam(name = "lowerBoundNum", required = false) double lowerBoundNum,
      @RequestParam(name = "highAlertSoundEnabled", required = false) boolean highAlertSoundEnabled,
      @RequestParam(name = "lowAlertSoundEnabled", required = false) boolean lowAlertSoundEnabled,
      Model model) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String formattedDateTime = currentDateTime.format(formatter);
    Profile profile =
        Profile.builder()
            .language("EN")
            .createdAt(formattedDateTime)
            .timeFormat(hoursFormat) // enum here too
            .highAlertSoundEnabled(highAlertSoundEnabled)
            .highBoundLimit(highBoundNum)
            .lowAlertSoundEnabled(lowAlertSoundEnabled)
            .lowerBoundLimit(lowerBoundNum)
            .units(units) // mmol or mgdl replase here with ENUM later
            .build();

    Profile savedProfile = profileRepository.save(profile);

    return "redirect:/settings";
  }
}
