package com.sugarmonitor.controller;

import com.sugarmonitor.model.Profile;
import com.sugarmonitor.model.User;
import com.sugarmonitor.service.ProfileService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final ProfileService profileService;

  @GetMapping()
  @PreAuthorize("hasAuthority('ADMIN')")
  public String mainReportMenu(@AuthenticationPrincipal User user, Model model) {

    Profile userProfile = profileService.getProfile();

    model.addAttribute("preferableUnits", userProfile.getUnits());
    model.addAttribute("preferableTimeFormat", userProfile.getTimeFormat());
    model.addAttribute("enableHighAlertSound", userProfile.isHighAlertSoundEnabled());
    model.addAttribute("enableLowAlertSound", userProfile.isLowAlertSoundEnabled());
    model.addAttribute("highBoundNum", userProfile.getHighBoundLimit());
    model.addAttribute("lowerBoundNum", userProfile.getLowerBoundLimit());
    model.addAttribute("user", user);
    model.addAttribute("profile", userProfile);

    return "profile";
  }

  @PostMapping("/update")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String updateProfile(
      @AuthenticationPrincipal User user,
      @RequestParam(name = "language", required = false, defaultValue = "EN") String language,
      @RequestParam(name = "units", required = false) String units,
      @RequestParam(name = "hours", required = false, defaultValue = "24") int hoursFormat,
      @RequestParam(name = "highBoundNum", required = false, defaultValue = "10.0")
          double highBoundNum,
      @RequestParam(name = "lowerBoundNum", required = false, defaultValue = "4.5")
          double lowerBoundNum,
      @RequestParam(name = "highAlertSoundEnabled", required = false) boolean highAlertSoundEnabled,
      @RequestParam(name = "lowAlertSoundEnabled", required = false) boolean lowAlertSoundEnabled,
      Model model) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String formattedDateTime = currentDateTime.format(formatter);
    Profile profile =
        Profile.builder()
            .language(language)
            .createdAt(formattedDateTime)
            .timeFormat(hoursFormat) // enum here too (disabled on UI for now)
            .highAlertSoundEnabled(highAlertSoundEnabled)
            .highBoundLimit(highBoundNum)
            .lowAlertSoundEnabled(lowAlertSoundEnabled)
            .lowerBoundLimit(lowerBoundNum)
            .units(units) // mmol or mgdl replase here with ENUM later
            .build();

    profileService.save(profile);

    return "redirect:/settings?lang=" + language;
  }
}
