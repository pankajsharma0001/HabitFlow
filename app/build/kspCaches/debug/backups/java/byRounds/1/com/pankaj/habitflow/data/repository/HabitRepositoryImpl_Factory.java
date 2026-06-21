package com.pankaj.habitflow.data.repository;

import com.pankaj.habitflow.data.local.dao.HabitDao;
import com.pankaj.habitflow.data.local.dao.HabitRecordDao;
import com.pankaj.habitflow.notification.AlarmScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class HabitRepositoryImpl_Factory implements Factory<HabitRepositoryImpl> {
  private final Provider<HabitDao> habitDaoProvider;

  private final Provider<HabitRecordDao> recordDaoProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public HabitRepositoryImpl_Factory(Provider<HabitDao> habitDaoProvider,
      Provider<HabitRecordDao> recordDaoProvider, Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.habitDaoProvider = habitDaoProvider;
    this.recordDaoProvider = recordDaoProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  @Override
  public HabitRepositoryImpl get() {
    return newInstance(habitDaoProvider.get(), recordDaoProvider.get(), alarmSchedulerProvider.get());
  }

  public static HabitRepositoryImpl_Factory create(Provider<HabitDao> habitDaoProvider,
      Provider<HabitRecordDao> recordDaoProvider, Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new HabitRepositoryImpl_Factory(habitDaoProvider, recordDaoProvider, alarmSchedulerProvider);
  }

  public static HabitRepositoryImpl newInstance(HabitDao habitDao, HabitRecordDao recordDao,
      AlarmScheduler alarmScheduler) {
    return new HabitRepositoryImpl(habitDao, recordDao, alarmScheduler);
  }
}
