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
import android.os.Build;
import android.os.UserHandle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.service.notification.NotificationListenerService.NOTIFICATION_CHANNEL_OR_GROUP_ADDED;
import static android.service.notification.NotificationListenerService.NOTIFICATION_CHANNEL_OR_GROUP_DELETED;
import static android.service.notification.NotificationListenerService.NOTIFICATION_CHANNEL_OR_GROUP_UPDATED;

/**
 * Represents a channel modified event
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public final class ChannelEvent {

    @IntDef(value = {
            NOTIFICATION_CHANNEL_OR_GROUP_ADDED,
            NOTIFICATION_CHANNEL_OR_GROUP_UPDATED,
            NOTIFICATION_CHANNEL_OR_GROUP_DELETED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChannelEventType {}

    private String packageName;
    private UserHandle user;
    private NotificationChannel notificationChannel;
    private int eventType;

    /**
     * Constructs a new channel modified event
     */
    public ChannelEvent(@NonNull String packageName,
                        @NonNull UserHandle user,
                        @NonNull NotificationChannel channel,
                        @ChannelEventType int eventType) {
        this.packageName = packageName;
        this.user = user;
        this.notificationChannel = channel;
        this.eventType = eventType;
    }

    /**
     * Returns the channel of this event
     */
    @NonNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns the user of this event
     */
    @NonNull
    public UserHandle getUser() {
        return user;
    }

    /**
     * Returns the notification channel of this event
     */
    @NonNull
    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    /**
     * Returns the event type of this event
     */
    @ChannelEventType
    public int getEventType() {
        return eventType;
    }
}
