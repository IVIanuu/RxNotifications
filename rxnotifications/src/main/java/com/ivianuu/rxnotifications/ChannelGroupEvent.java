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

import android.app.NotificationChannelGroup;
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
 * Represents a channel group event
 */
@RequiresApi(Build.VERSION_CODES.O)
public final class ChannelGroupEvent {

    @IntDef(value = {
            NOTIFICATION_CHANNEL_OR_GROUP_ADDED,
            NOTIFICATION_CHANNEL_OR_GROUP_UPDATED,
            NOTIFICATION_CHANNEL_OR_GROUP_DELETED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChannelGroupEventType{}

    private String packageName;
    private UserHandle user;
    private NotificationChannelGroup notificationChannelGroup;
    private int eventType;

    /**
     * Constructs a new channel group event
     */
    public ChannelGroupEvent(@NonNull String packageName,
                             @NonNull UserHandle user,
                             @NonNull NotificationChannelGroup group,
                             int eventType) {
        this.packageName = packageName;
        this.user = user;
        this.notificationChannelGroup = group;
        this.eventType = eventType;
    }

    /**
     * Returns the package name of this event
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
     * Returns the channel group of this event
     */
    @NonNull
    public NotificationChannelGroup getNotificationChannelGroup() {
        return notificationChannelGroup;
    }

    /**
     * Returns the type of this event
     */
    @ChannelGroupEventType
    public int getEventType() {
        return eventType;
    }
}
