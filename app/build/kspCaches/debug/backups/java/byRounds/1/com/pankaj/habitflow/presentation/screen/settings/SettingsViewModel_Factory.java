package com.pankaj.habitflow.presentation.screen.settings;

import com.pankaj.habitflow.data.local.ThemePreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ThemePreferences> themePreferencesProvider;

  public SettingsViewModel_Factory(Provider<ThemePreferences> themePreferencesProvider) {
    this.themePreferencesProvider = themePreferencesProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(themePreferencesProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ThemePreferences> themePreferencesProvider) {
    return new SettingsViewModel_Factory(themePreferencesProvider);
  }

  public static SettingsViewModel newInstance(ThemePreferences themePreferences) {
    return new SettingsViewModel(themePreferences);
  }
}
