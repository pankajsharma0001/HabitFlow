package com.pankaj.habitflow.presentation.screen.habits;

import androidx.lifecycle.SavedStateHandle;
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
public final class AddEditHabitViewModel_Factory implements Factory<AddEditHabitViewModel> {
  private final Provider<HabitRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public AddEditHabitViewModel_Factory(Provider<HabitRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public AddEditHabitViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static AddEditHabitViewModel_Factory create(Provider<HabitRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new AddEditHabitViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static AddEditHabitViewModel newInstance(HabitRepository repository,
      SavedStateHandle savedStateHandle) {
    return new AddEditHabitViewModel(repository, savedStateHandle);
  }
}
