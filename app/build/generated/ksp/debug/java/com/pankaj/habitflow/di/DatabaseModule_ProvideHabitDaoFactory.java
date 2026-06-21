package com.pankaj.habitflow.di;

import com.pankaj.habitflow.data.local.AppDatabase;
import com.pankaj.habitflow.data.local.dao.HabitDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DatabaseModule_ProvideHabitDaoFactory implements Factory<HabitDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideHabitDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public HabitDao get() {
    return provideHabitDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideHabitDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideHabitDaoFactory(databaseProvider);
  }

  public static HabitDao provideHabitDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHabitDao(database));
  }
}
