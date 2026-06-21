package com.pankaj.habitflow.notification;

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
public final class ReminderReceiver_MembersInjector implements MembersInjector<ReminderReceiver> {
  private final Provider<NotificationHelper> notificationHelperProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public ReminderReceiver_MembersInjector(Provider<NotificationHelper> notificationHelperProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.notificationHelperProvider = notificationHelperProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  public static MembersInjector<ReminderReceiver> create(
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new ReminderReceiver_MembersInjector(notificationHelperProvider, alarmSchedulerProvider);
  }

  @Override
  public void injectMembers(ReminderReceiver instance) {
    injectNotificationHelper(instance, notificationHelperProvider.get());
    injectAlarmScheduler(instance, alarmSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.pankaj.habitflow.notification.ReminderReceiver.notificationHelper")
  public static void injectNotificationHelper(ReminderReceiver instance,
      NotificationHelper notificationHelper) {
    instance.notificationHelper = notificationHelper;
  }

  @InjectedFieldSignature("com.pankaj.habitflow.notification.ReminderReceiver.alarmScheduler")
  public static void injectAlarmScheduler(ReminderReceiver instance,
      AlarmScheduler alarmScheduler) {
    instance.alarmScheduler = alarmScheduler;
  }
}
