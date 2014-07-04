/*
 * Copyright (C) 2014 OpenSilk Productions LLC
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
package org.opensilk.cast.helpers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.opensilk.cast.CastServiceBinder;
import org.opensilk.cast.SilkCastService;

import java.util.WeakHashMap;

/**
 * Created by drew on 3/15/14.
 */
public class LocalCastServiceManager {

    /**
     * CastServiceBinder
     */
    public static CastServiceBinder sCastService;

    private static final WeakHashMap<Context, ServiceBinder> sConnectionMap;

    static {
        sConnectionMap = new WeakHashMap<>();
    }

    private LocalCastServiceManager() {
        //static
    }

    /**
     * @param context The {@link Context} to use
     * @param callback The {@link org.opensilk.cast.helpers.CastServiceConnectionCallback} to use
     * @return The new instance of {@link ServiceToken}
     */
    public static ServiceToken bindToService(final Context context, final CastServiceConnectionCallback callback) {
        final ContextWrapper contextWrapper;
        if (context instanceof Activity) {
            Activity realActivity = ((Activity)context).getParent();
            if (realActivity == null) {
                realActivity = (Activity) context;
            }
            contextWrapper = new ContextWrapper(realActivity);
        } else {
            contextWrapper = new ContextWrapper(context);
        }
        final ServiceBinder binder = new ServiceBinder(callback);
        if (contextWrapper.bindService(
                new Intent().setClass(contextWrapper, SilkCastService.class)
                .setAction(SilkCastService.ACTION_BIND_LOCAL), binder, Context.BIND_AUTO_CREATE)) {
            sConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;
    }

    /**
     * @param token The {@link ServiceToken} to unbind from
     */
    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mWrappedContext;
        final ServiceBinder mBinder = sConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        mContextWrapper.unbindService(mBinder);
        if (sConnectionMap.isEmpty()) {
            sCastService = null;
        }
    }

    public static final class ServiceBinder implements ServiceConnection {
        private final CastServiceConnectionCallback mCallback;

        public ServiceBinder(final CastServiceConnectionCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            sCastService = (CastServiceBinder) service;
            if (sCastService != null) {
                if (mCallback != null) {
                    mCallback.onCastServiceConnected();
                }
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            if (mCallback != null) {
                mCallback.onCastServiceDisconnected();
            }
            sCastService = null;
        }
    }

    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;
        public ServiceToken(final ContextWrapper context) {
            mWrappedContext = context;
        }
    }

}
