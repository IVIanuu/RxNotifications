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

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;

import com.ivianuu.rxserviceconnection.RxBinder;

/**
 * Rx notification listener service
 */
public final class RxNotificationListenerService extends NotificationListenerService {

    private static final String EXTRA_BIND_INTERNALLY = "bind_internally";

    interface ServiceCallbacks {
        void onNotificationPosted(@NonNull StatusBarNotification sbn);
        void onNotificationRemoved(@NonNull StatusBarNotification sbn);
        void onListenerConnectionChanged(boolean connected);
        void onNotificationChannelModified(@NonNull String pkg, @NonNull UserHandle user, @NonNull NotificationChannel channel, int modificationType);
        void onNotificationChannelGroupModified(@NonNull String pkg, @NonNull UserHandle user, @NonNull NotificationChannelGroup group, int modificationType);
        void onNotificationRankingUpdate(@NonNull RankingMap rankingMap);
        void onInterruptionFilterChanged(int interruptionFilter);
        void onListenerHintsChanged(int hints);
    }

    private RxBinder<RxNotificationListenerService> binder = new RxBinder<RxNotificationListenerService>() {
        @NonNull
        @Override
        public RxNotificationListenerService getService() {
            return RxNotificationListenerService.this;
        }
    };

    private RealNotificationListener notificationListener;

    private boolean listenerConnected;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationListener = new RealNotificationListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.hasExtra(EXTRA_BIND_INTERNALLY)) {
            // a rx notifications component wants to bind so return our own binder
            return binder;
        } else {
            // necessary to bind the notification listener
            return super.onBind(intent);
        }
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        listenerConnected = true;
        notificationListener.onListenerConnectionChanged(true);
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        listenerConnected = false;
        notificationListener.onListenerConnectionChanged(false);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        notificationListener.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        notificationListener.onNotificationRemoved(sbn);
    }

    @SuppressLint("NewApi")
    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
        notificationListener.onNotificationChannelModified(pkg, user, channel, modificationType);
    }

    @SuppressLint("NewApi")
    @Override
    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
        notificationListener.onNotificationChannelGroupModified(pkg, user, group, modificationType);
    }

    @SuppressLint("NewApi")
    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
        notificationListener.onNotificationRankingUpdate(rankingMap);
    }

    @SuppressLint("NewApi")
    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        super.onInterruptionFilterChanged(interruptionFilter);
        notificationListener.onInterruptionFilterChanged(interruptionFilter);
    }

    @SuppressLint("NewApi")
    @Override
    public void onListenerHintsChanged(int hints) {
        super.onListenerHintsChanged(hints);
        notificationListener.onListenerHintsChanged(hints);
    }

    boolean isListenerConnected() {
        return listenerConnected;
    }

    @NonNull
    RxNotificationListener getListener() {
        return notificationListener;
    }

    static Intent createBindingIntent(@NonNull Context context) {
        Intent intent = new Intent(context, RxNotificationListenerService.class);
        intent.putExtra(EXTRA_BIND_INTERNALLY, "");
        return intent;
    }
}
