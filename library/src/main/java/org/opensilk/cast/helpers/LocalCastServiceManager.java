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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.opensilk.cast.CastService;
import org.opensilk.cast.CastServiceBinder;

/**
 * Created by drew on 3/15/14.
 */
public class LocalCastServiceManager extends BaseCastServiceManager {

    private CastServiceBinder mService;

    public LocalCastServiceManager(Context context) {
        super(context);
    }

    public void bind() {
        mContext.bindService(new Intent(mContext, CastService.class)
                    .setAction(CastService.ACTION_BIND_LOCAL),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        mContext.unbindService(mServiceConnection);
    }

    public CastServiceBinder getService() {
        return mService;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (CastServiceBinder) service;
            if (mService != null) {
                if (mCallback != null) {
                    mCallback.onCastServiceConnected();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            if (mCallback != null) {
                mCallback.onCastServiceDisconnected();
            }
        }
    };

}
