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

package org.opensilk.cast;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.opensilk.cast.manager.MediaCastManager;


import java.util.HashSet;
import java.util.Set;

import static org.opensilk.cast.util.LogUtils.makeLogTag;
import static org.opensilk.cast.util.LogUtils.LOGD;

/**
 * Created by drew on 3/15/14.
 */
public class SilkCastService extends Service {
    private static final String TAG = makeLogTag(SilkCastService.class);

    /**
     * Action for same process binding
     */
    public static final String ACTION_BIND_LOCAL = "bind_local";

    /**
     * Action for remote process binding
     */
    public static final String ACTION_BIND_REMOTE = "bind_remote";

    /**
     * Local binder
     */
    CastServiceBinder mLocalBinder;

    /**
     * Remote binder
     */
    CastServiceImpl mRemoteBinder;

    /**
     * Remote messager listeners
     * Local clients should just register a CastConsumer directly with CastManager
     */
    Set<Messenger> mMessengers = new HashSet<>();

    /**
     * Local callback handler, will forward events to remote messengers
     */
    CastServiceConsumer mCastManagerListener;

    /**
     * CastManager, this is essentially a singleton
     */
    MediaCastManager mCastManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION_BIND_LOCAL.equals(intent.getAction())) {
            LOGD(TAG, "onBind() local");
            return mLocalBinder;
        } else if (ACTION_BIND_REMOTE.equals(intent.getAction())) {
            LOGD(TAG, "onBind() remote");
            return mRemoteBinder;
        }
        throw new RuntimeException("Action not defined");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LOGD(TAG, "onUnbind() " + intent.toString());
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        LOGD(TAG, "onCreate()");
        super.onCreate();
        mRemoteBinder = new CastServiceImpl(this);
        mLocalBinder = new CastServiceBinder(this);
        mCastManagerListener = new CastServiceConsumer(this);
        mCastManager = MediaCastManager.initialize(getApplicationContext(),
                getApplicationContext().getString(R.string.cast_id), null);
        if (BuildConfig.DEBUG) {
            mCastManager.enableFeatures(MediaCastManager.FEATURE_DEBUGGING);
        }
        // We are streaming /from/ the device so it needs to exit
        mCastManager.setStopOnDisconnect(true);
        mCastManager.addCastConsumer(mCastManagerListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        throw new RuntimeException("CastService is bind only");
    }

    @Override
    public void onDestroy() {
        LOGD(TAG, "onDestroy()");
        super.onDestroy();
        mRemoteBinder = null;
        mLocalBinder = null;
        mCastManager.removeCastConsumer(mCastManagerListener);
        mCastManagerListener = null;
        mCastManager = null;
    }

    /**
     * Handle registering and unregistering remote messengers
     *
     * This seems a little round about, need to look into
     * passing a raw IBinder to add to mMessengers
     */

    public static final int MESSENGER_REGISTER = 1;
    public static final int MESSENGER_UNREGISTER = 2;

    final Messenger mCallbackMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSENGER_REGISTER:
                    mMessengers.add(msg.replyTo);
                    break;
                case MESSENGER_UNREGISTER:
                    mMessengers.remove(msg.replyTo);
                    break;
            }
        }
    });
}
