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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * Created by drew on 3/15/14.
 */
public class CastServiceHelper {

    public interface ConnectionCallback {
        public void onCastServiceConnected();
        public void onCastServiceDisconnected();
    }

    private final Context mContext;
    private Messenger mMessenger;
    private ConnectionCallback mCallback;
    private ICastService mService;

    public CastServiceHelper(Context context) {
        mContext = context;
    }

    public void setMessenger(Messenger m) {
        mMessenger = m;
    }

    public void setCallback(ConnectionCallback cb) {
        mCallback = cb;
    }

    public void bindLocal() {
        mContext.bindService(new Intent(mContext, CastService.class)
                .setAction(CastService.ACTION_BIND_LOCAL), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void bindRemote() {
        mContext.bindService(new Intent(mContext, CastService.class)
                .setAction(CastService.ACTION_BIND_REMOTE), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        unregisterMessenger(mMessenger);
        mContext.unbindService(mServiceConnection);
    }

    public boolean registerMessenger(Messenger messenger) {
        if (messenger == null) {
            return false;
        }
        try {
            Messenger serviceMessenger = new Messenger(mService.getMessenger());
            Message msg = Message.obtain(null, CastService.MESSENGER_REGISTER);
            msg.replyTo = messenger;
            serviceMessenger.send(msg);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean unregisterMessenger(Messenger messenger) {
        if (messenger == null) {
            return false;
        }
        try {
            Messenger serviceMessenger = new Messenger(mService.getMessenger());
            Message msg = Message.obtain(null, CastService.MESSENGER_UNREGISTER);
            msg.replyTo = messenger;
            serviceMessenger.send(msg);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = CastServiceImpl.asInterface(service);
            if (mService != null) {
                registerMessenger(mMessenger);
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
