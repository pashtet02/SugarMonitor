package com.sugarmonitor.service;

import com.sugarmonitor.model.Profile;

public interface ProfileService {
  double getLowerBoundLimit();

  double getHighBoundLimit();

  Profile getProfile();

  Profile save(Profile newProfile);

  int getYAxisGraphMinLimit();

  int getYAxisGraphMaxLimit();

  int getYAxisGraphStep();
}
