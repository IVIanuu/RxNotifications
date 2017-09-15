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

import android.service.notification.StatusBarNotification;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.ivianuu.rxnotifications.NotificationEvent.NotificationEventType.NOTIFICATION_POSTED;
import static com.ivianuu.rxnotifications.NotificationEvent.NotificationEventType.NOTIFICATION_REMOVED;

/**
 * Represents a notification event
 */
public final class NotificationEvent {

    @IntDef(value = {NOTIFICATION_POSTED, NOTIFICATION_REMOVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotificationEventType {
        int NOTIFICATION_POSTED = 0;
        int NOTIFICATION_REMOVED = 2;
    }

    private StatusBarNotification statusBarNotification;
    private int eventType;

    /**
     * Constructs a new notification event
     */
    public NotificationEvent(@NonNull StatusBarNotification statusBarNotification,
                             @NotificationEventType int eventType) {
        this.statusBarNotification = statusBarNotification;
        this.eventType = eventType;
    }

    /**
     * Returns the status bar notification of this event
     */
    @NonNull
    public StatusBarNotification getStatusBarNotification() {
        return statusBarNotification;
    }

    /**
     * Returns the event type
     */
    @NotificationEventType
    public int getEventType() {
        return eventType;
    }

}
