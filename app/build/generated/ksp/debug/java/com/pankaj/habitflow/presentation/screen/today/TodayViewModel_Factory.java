package com.pankaj.habitflow.presentation.screen.today;

import com.pankaj.habitflow.domain.repository.HabitRepository;
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
public final class TodayViewModel_Factory implements Factory<TodayViewModel> {
  private final Provider<HabitRepository> repositoryProvider;

  public TodayViewModel_Factory(Provider<HabitRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TodayViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static TodayViewModel_Factory create(Provider<HabitRepository> repositoryProvider) {
    return new TodayViewModel_Factory(repositoryProvider);
  }

  public static TodayViewModel newInstance(HabitRepository repository) {
    return new TodayViewModel(repository);
  }
}
