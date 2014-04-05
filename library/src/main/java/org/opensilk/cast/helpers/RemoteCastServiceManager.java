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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.opensilk.cast.SilkCastService;
import org.opensilk.cast.CastServiceImpl;
import org.opensilk.cast.ICastService;

/**
 * Created by drew on 3/15/14.
 */
public class RemoteCastServiceManager extends BaseCastServiceManager {

    private Messenger mMessenger;
    private ICastService mService;

    public RemoteCastServiceManager(Context context, Messenger messenger) {
        super(context);
        mMessenger = messenger;
    }

    public void bind() {
        mContext.bindService(new Intent(mContext, SilkCastService.class)
                    .setAction(SilkCastService.ACTION_BIND_REMOTE),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        unregisterMessenger(mMessenger);
        mContext.unbindService(mServiceConnection);
    }

    public ICastService getService() {
        return mService;
    }

    public boolean registerMessenger(Messenger messenger) {
        if (messenger == null) {
            return false;
        }
        try {
            if (mService != null) {
                Messenger serviceMessenger = new Messenger(mService.getMessenger());
                Message msg = Message.obtain(null, SilkCastService.MESSENGER_REGISTER);
                if (msg != null) {
                    msg.replyTo = messenger;
                    serviceMessenger.send(msg);
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean unregisterMessenger(Messenger messenger) {
        if (messenger == null) {
            return false;
        }
        try {
            if (mService != null) {
                Messenger serviceMessenger = new Messenger(mService.getMessenger());
                Message msg = Message.obtain(null, SilkCastService.MESSENGER_UNREGISTER);
                if (msg != null) {
                    msg.replyTo = messenger;
                    serviceMessenger.send(msg);
                    return true;
                }
            }
            return false;
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
