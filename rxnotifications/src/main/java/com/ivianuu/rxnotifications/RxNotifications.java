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
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.ivianuu.rxserviceconnection.RxServiceConnection;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;

/**
 * Wraps a notification listener service in observables
 */
public class RxNotifications {

    @SuppressLint("StaticFieldLeak")
    private static RxNotifications instance;

    private Context context;

    private RxNotificationListenerService service;

    /**
     * Constructs a new rx notifications instance
     */
    private RxNotifications(@NonNull Context context) {
        this.context = context;
    }

    public static RxNotifications get(@NonNull Context context) {
        if (instance == null) {
            instance = new RxNotifications(context.getApplicationContext());
        }

        return instance;
    }

    /**
     * Returns if we have the notification listener permission
     */
    public boolean hasPermission() {
        return Util.hasNotificationListenerPermission(context);
    }

    /**
     * Returns the notification listener
     */
    @CheckResult @NonNull
    public Single<RxNotificationListener> getNotificationListener() {
        return Single.create(e -> {
            if (service != null) {
                if (!e.isDisposed()) {
                    e.onSuccess(service.getListener());
                }
            } else {
                RxServiceConnection.<RxNotificationListenerService>bind(
                        context, RxNotificationListenerService.createBindingIntent(context))
                        .subscribe(service -> {
                            RxNotifications.this.service = service;
                            if (!e.isDisposed()) {
                                e.onSuccess(service.getListener());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!e.isDisposed()) {
                                    e.onError(throwable);
                                }
                            }
                        });
            }
        });
    }

}
