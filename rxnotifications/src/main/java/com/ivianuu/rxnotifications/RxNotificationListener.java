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

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Reactive notification listener
 */
public interface RxNotificationListener {

    // LISTENER CONNECTION STATE

    /**
     * Emits on listener connection changes
     */
    @CheckResult @NonNull
    Observable<Boolean> observeListenerConnected();

    /**
     * Returns if the listener is currently connected
     */
    @CheckResult @NonNull
    Single<Boolean> isListenerConnected();

    // NOTIFICATIONS

    /**
     * Emits when a new notification was posted or removed
     */
    @CheckResult @NonNull
    Flowable<NotificationEvent> observeNotificationEvents();

    /**
     * Emits on active notification changes
     */
    @CheckResult @NonNull
    Flowable<List<StatusBarNotification>> observeActiveNotifications();

    /**
     * Returns current active notifications
     */
    @CheckResult @NonNull
    Single<List<StatusBarNotification>> getActiveNotifications();

    /**
     * Returns current snoozed notifications
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    Single<List<StatusBarNotification>> getSnoozedNotifications();

    /**
     * Cancels the provided notification
     */
    @CheckResult @NonNull
    Completable cancelNotification(@NonNull StatusBarNotification statusBarNotification);

    /**
     * Cancels the provided notification
     */
    @CheckResult @NonNull
    Completable cancelNotifications(@NonNull List<StatusBarNotification> statusBarNotification);

    /**
     * Cancels all notifications
     */
    @CheckResult @NonNull
    Completable cancelAllNotifications();

    /**
     * Sets the notification as shown
     */
    @CheckResult @NonNull
    Completable setNotificationShown(@NonNull StatusBarNotification sbn);

    /**
     * Sets the notifications as shown
     */
    @CheckResult @NonNull
    Completable setNotificationsShown(@NonNull List<StatusBarNotification> sbns);

    /**
     * Snoozes the notification for the desired duration
     */
    @CheckResult @NonNull
    Completable snoozeNotification(@NonNull StatusBarNotification sbn, long duration);

    /**
     * Snoozes the notification for the desired duration
     */
    @CheckResult @NonNull
    Completable snoozeNotifications(@NonNull List<StatusBarNotification> sbns, long duration);

    // CHANNELS

    /**
     * Emits on channel events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    Flowable<ChannelEvent> observeChannelEvents();

    /**
     * Returns all notifications channels from the package by the user
     */
    @CheckResult @NonNull
    Single<List<NotificationChannel>> getNotificationChannels(@NonNull String pkg, @NonNull UserHandle user);

    /**
     * Updates the notification channel
     */
    @CheckResult @NonNull
    Completable updateNotificationChannel(@NonNull String pkg, @NonNull UserHandle user, @NonNull NotificationChannel channel);

    // CHANNEL GROUPS

    /**
     * Emits on channel group events
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @CheckResult @NonNull
    Flowable<ChannelGroupEvent> observeChannelGroupEvents();

    /**
     * Returns the notification channel groups for the package by the user
     */
    @CheckResult @NonNull
    Single<List<NotificationChannelGroup>> getNotificationChannelGroups(@NonNull String pkg, @NonNull UserHandle user);

    // INTERRUPTION FILTER

    /**
     * Emits on interruption filter changes
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    Flowable<Integer> observeInterruptionFilter();

    /**
     * Returns the current interruption filter
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    Single<Integer> getInterruptionFilter();

    /**
     * Try's to the set the interruption filter
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @CheckResult @NonNull
    Completable requestInterruptionFilter(int interruptionFilter);

    // LISTENER HINTS

    /**
     * Emits on listener hint changes
     */
    @CheckResult @NonNull
    Flowable<Integer> observeListenerHints();

    /**
     * Returns the current listener hints
     */
    @CheckResult @NonNull
    Single<Integer> getListenerHints();

    /**
     * Try's to set the listener hints
     */
    @CheckResult @NonNull
    Completable requestListenerHints(int hints);

    // RANKING

    /**
     * Emits on ranking changes
     */
    @CheckResult @NonNull
    Flowable<NotificationListenerService.RankingMap> observeRanking();

    /**
     * Returns the current ranking
     */
    @CheckResult @NonNull
    Single<NotificationListenerService.RankingMap> getRanking();

}
