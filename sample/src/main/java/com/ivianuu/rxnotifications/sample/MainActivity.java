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

package com.ivianuu.rxnotifications.sample;

import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ivianuu.rxnotifications.NotificationEvent;
import com.ivianuu.rxnotifications.RxNotificationListener;
import com.ivianuu.rxnotifications.RxNotifications;

import java.util.List;
import java.util.Timer;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.NotificationManager.INTERRUPTION_FILTER_ALARMS;
import static android.app.NotificationManager.INTERRUPTION_FILTER_ALL;
import static android.app.NotificationManager.INTERRUPTION_FILTER_NONE;
import static android.app.NotificationManager.INTERRUPTION_FILTER_PRIORITY;
import static android.app.NotificationManager.INTERRUPTION_FILTER_UNKNOWN;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RxNotifications rxNotifications = RxNotifications.get(this);
        Disposable disposable = rxNotifications.getNotificationListener()
                .subscribe(rxNotificationListener -> {
                    Log.d("rxnotifications", "listener available");
                    startListeníng(rxNotificationListener);
                });
        compositeDisposable.add(disposable);
    }

    private void startListeníng(final RxNotificationListener rxNotificationListener) {
        Disposable notificationsEventsDisposable = rxNotificationListener.observeNotificationEvents()
                .subscribe(notificationEvent -> {
                    switch (notificationEvent.getEventType()) {
                        case NotificationEvent.NotificationEventType.NOTIFICATION_POSTED:
                            Log.d("rxnotifications", "posted "
                                    + notificationEvent.getStatusBarNotification().getPackageName());
                            break;
                        case NotificationEvent.NotificationEventType.NOTIFICATION_REMOVED:
                            Log.d("rxnotifications", "removed "
                                    + notificationEvent.getStatusBarNotification().getPackageName());
                            break;
                    }
                });
        compositeDisposable.add(notificationsEventsDisposable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clean up
        compositeDisposable.clear();
    }
}
