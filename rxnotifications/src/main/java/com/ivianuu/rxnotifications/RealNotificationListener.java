/*
 * Copyright 2017 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.rxnotifications;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.os.Build;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;

import static com.ivianuu.rxnotifications.NotificationEvent.NotificationEventType.NOTIFICATION_POSTED;
import static com.ivianuu.rxnotifications.NotificationEvent.NotificationEventType.NOTIFICATION_REMOVED;
import static com.ivianuu.rxnotifications.Preconditions.checkNotNull;

/**
 * Implementation of a rx notification listener
 */
class RealNotificationListener implements RxNotificationListener, RxNotificationListenerService.ServiceCallbacks {

    private RxNotificationListenerService service;

    private PublishSubject<Boolean> listenerConnectedSubject = PublishSubject.create();
    private PublishProcessor<NotificationEvent> notificationEventsSubject = PublishProcessor.create();
    private BehaviorProcessor<List<StatusBarNotification>> activeNotificationsSubject = BehaviorProcessor.create();
    private PublishProcessor<ChannelEvent> channelEventsSubject = PublishProcessor.create();
    private PublishProcessor<ChannelGroupEvent> channelGroupEventsSubject = PublishProcessor.create();
    private BehaviorProcessor<Integer> interruptionFilterSubject = BehaviorProcessor.create();
    private BehaviorProcessor<Integer> listenerHintsSubject = BehaviorProcessor.create();
    private BehaviorProcessor<NotificationListenerService.RankingMap> rankingSubject = BehaviorProcessor.create();

    RealNotificationListener(@NonNull RxNotificationListenerService service) {
        this.service = service;
    }

    // SERVICE CALLBACKS
    @Override
    public void onNotificationPosted(@NonNull StatusBarNotification sbn) {
        notificationEventsSubject.onNext(new NotificationEvent(sbn, NOTIFICATION_POSTED));
        activeNotificationsSubject.onNext(getActiveNotificationsInternal());
    }

    @Override
    public void onNotificationRemoved(@NonNull StatusBarNotification sbn) {
        notificationEventsSubject.onNext(new NotificationEvent(sbn, NOTIFICATION_REMOVED));
        activeNotificationsSubject.onNext(getActiveNotificationsInternal());
    }

    @Override
    public void onListenerConnectionChanged(boolean connected) {
        listenerConnectedSubject.onNext(connected);
        if (connected) {
            activeNotificationsSubject.onNext(getActiveNotificationsInternal());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                interruptionFilterSubject.onNext(service.getCurrentInterruptionFilter());
                listenerHintsSubject.onNext(service.getCurrentListenerHints());
                rankingSubject.onNext(service.getCurrentRanking());
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNotificationChannelModified(@NonNull String pkg, @NonNull UserHandle user, @NonNull NotificationChannel channel, int modificationType) {
        channelEventsSubject.onNext(new ChannelEvent(pkg, user, channel, modificationType));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNotificationChannelGroupModified(@NonNull String pkg, @NonNull UserHandle user, @NonNull NotificationChannelGroup group, int modificationType) {
        channelGroupEventsSubject.onNext(new ChannelGroupEvent(pkg, user, group, modificationType));
    }

    @Override
    public void onNotificationRankingUpdate(@NonNull NotificationListenerService.RankingMap rankingMap) {
        rankingSubject.onNext(rankingMap);
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        interruptionFilterSubject.onNext(interruptionFilter);
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        listenerHintsSubject.onNext(hints);
    }

    private List<StatusBarNotification> getActiveNotificationsInternal() {
        List<StatusBarNotification> notifications = new ArrayList<>();
        StatusBarNotification[] activeNotifications = service.getActiveNotifications();
        if (activeNotifications != null) {
            notifications.addAll(Arrays.asList(activeNotifications));
        }

        return notifications;
    }

    // RX NOTIFICATION LISTENER

    @CheckResult @NonNull
    @Override
    public Observable<Boolean> observeListenerConnected() {
        return listenerConnectedSubject;
    }

    @CheckResult @NonNull
    @Override
    public Single<Boolean> isListenerConnected() {
        return Single.just(service.isListenerConnected());
    }

    @CheckResult @NonNull
    @Override
    public Flowable<NotificationEvent> observeNotificationEvents() {
        return notificationEventsSubject;
    }

    @CheckResult @NonNull
    @Override
    public Flowable<List<StatusBarNotification>> observeActiveNotifications() {
        return activeNotificationsSubject;
    }

    @CheckResult @NonNull
    @Override
    public Single<List<StatusBarNotification>> getActiveNotifications() {
        List<StatusBarNotification> notifications = new ArrayList<>();
        StatusBarNotification[] activeNotifications = service.getActiveNotifications();
        if (activeNotifications != null) {
            notifications.addAll(Arrays.asList(activeNotifications));
        }
        return Single.just(notifications);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Single<List<StatusBarNotification>> getSnoozedNotifications() {
        List<StatusBarNotification> notifications = new ArrayList<>();
        StatusBarNotification[] snoozedNotifications = service.getSnoozedNotifications();
        if (snoozedNotifications != null) {
            notifications.addAll(Arrays.asList(snoozedNotifications));
        }
        return Single.just(notifications);
    }

    @CheckResult @NonNull
    @Override
    public Completable cancelNotification(@NonNull StatusBarNotification statusBarNotification) {
        return cancelNotifications(Collections.singletonList(statusBarNotification));
    }

    @CheckResult @NonNull
    @Override
    public Completable cancelNotifications(@NonNull final List<StatusBarNotification> statusBarNotification) {

        return Completable.fromCallable(() -> {
            for (StatusBarNotification sbn : statusBarNotification) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    service.cancelNotification(sbn.getKey());
                } else {
                    service.cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
                }
            }
            return new Object();
        });
    }

    @CheckResult @NonNull
    @Override
    public Completable cancelAllNotifications() {
        return Completable.fromCallable(() -> {
            service.cancelAllNotifications();
            return new Object();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @CheckResult @NonNull
    @Override
    public Completable setNotificationShown(@NonNull StatusBarNotification sbn) {
        return setNotificationsShown(Collections.singletonList(sbn));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @CheckResult @NonNull
    @Override
    public Completable setNotificationsShown(@NonNull final List<StatusBarNotification> sbns) {
        checkNotNull(sbns, "sbn == null");
        return Completable.fromCallable(() -> {
            String[] keys = new String[]{};
            for (int i = 0; i < sbns.size(); i++) {
                keys[i] = sbns.get(i).getKey();
            }
            service.setNotificationsShown(keys);
            return new Object();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Completable snoozeNotification(@NonNull StatusBarNotification sbn, long duration) {
        return snoozeNotifications(Collections.singletonList(sbn), duration);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Completable snoozeNotifications(@NonNull final List<StatusBarNotification> sbns, final long duration) {
        checkNotNull(sbns, "sbns == null");
        return Completable.fromCallable(() -> {
            for (StatusBarNotification sbn : sbns) {
                service.snoozeNotification(sbn.getKey(), duration);
            }
            return new Object();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Flowable<ChannelEvent> observeChannelEvents() {
        return channelEventsSubject;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Single<List<NotificationChannel>> getNotificationChannels(@NonNull String pkg, @NonNull UserHandle user) {
        checkNotNull(pkg, "pkg == null");
        checkNotNull(user, "userHandle == null");
        return Single.just(service.getNotificationChannels(pkg, user));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Completable updateNotificationChannel(@NonNull final String pkg,
                                                 @NonNull final UserHandle user,
                                                 @NonNull final NotificationChannel channel) {
        checkNotNull(pkg, "pkg == null");
        checkNotNull(user, "userHandle == null");
        checkNotNull(channel, "channel == null");
        return Completable.fromCallable(() -> {
            service.updateNotificationChannel(pkg, user, channel);
            return new Object();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Flowable<ChannelGroupEvent> observeChannelGroupEvents() {
        return channelGroupEventsSubject;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    @Override
    public Single<List<NotificationChannelGroup>> getNotificationChannelGroups(@NonNull String pkg,
                                                                               @NonNull UserHandle user) {
        checkNotNull(pkg, "pkg == null");
        checkNotNull(user, "userHandle == null");
        return Single.just(service.getNotificationChannelGroups(pkg, user));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Flowable<Integer> observeInterruptionFilter() {
        return interruptionFilterSubject;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Single<Integer> getInterruptionFilter() {
        return Single.just(service.getCurrentInterruptionFilter());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Completable requestInterruptionFilter(final int interruptionFilter) {
        return Completable.fromCallable(() -> {
            service.requestInterruptionFilter(interruptionFilter);
            return new Object();
        });
    }

    @CheckResult @NonNull
    @Override
    public Flowable<Integer> observeListenerHints() {
        return listenerHintsSubject;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Single<Integer> getListenerHints() {
        return Single.just(service.getCurrentListenerHints());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Completable requestListenerHints(final int hints) {
        return Completable.fromCallable(() -> {
            service.requestListenerHints(hints);
            return new Object();
        });
    }

    @CheckResult @NonNull
    @Override
    public Flowable<NotificationListenerService.RankingMap> observeRanking() {
        return rankingSubject;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    @Override
    public Single<NotificationListenerService.RankingMap> getRanking() {
        return Single.just(service.getCurrentRanking());
    }

}
