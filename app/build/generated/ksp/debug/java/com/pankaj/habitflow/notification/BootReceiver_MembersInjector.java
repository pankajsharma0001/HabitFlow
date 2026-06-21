package com.pankaj.habitflow.notification;

import com.pankaj.habitflow.data.local.dao.HabitDao;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<HabitDao> habitDaoProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public BootReceiver_MembersInjector(Provider<HabitDao> habitDaoProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.habitDaoProvider = habitDaoProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  public static MembersInjector<BootReceiver> create(Provider<HabitDao> habitDaoProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new BootReceiver_MembersInjector(habitDaoProvider, alarmSchedulerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectHabitDao(instance, habitDaoProvider.get());
    injectAlarmScheduler(instance, alarmSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.pankaj.habitflow.notification.BootReceiver.habitDao")
  public static void injectHabitDao(BootReceiver instance, HabitDao habitDao) {
    instance.habitDao = habitDao;
  }

  @InjectedFieldSignature("com.pankaj.habitflow.notification.BootReceiver.alarmScheduler")
  public static void injectAlarmScheduler(BootReceiver instance, AlarmScheduler alarmScheduler) {
    instance.alarmScheduler = alarmScheduler;
  }
}
