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

import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

import static org.opensilk.cast.util.LogUtils.makeLogTag;
import static org.opensilk.cast.util.LogUtils.LOGD;

/**
 * Implementation for remote processes to access CastService
 *
 * Created by drew on 3/15/14.
 */
public class CastServiceImpl extends ICastService.Stub {
    private static final String TAG = makeLogTag(CastServiceImpl.class);

    private final WeakReference<SilkCastService> mService;
    private final ICastManager mCastManager;

    CastServiceImpl(SilkCastService service) {
        mService = new WeakReference<>(service);
        mCastManager = new CastManagerImpl(service);
    }

    /**
     * @return proxy implementation of ICastManager
     * @throws RemoteException
     */
    @Override
    public ICastManager getCastManager() throws RemoteException {
        return mCastManager;
    }

    /**
     * Register a remote messenger to receive callbacks from the CastManager on
     * @param messenger binder representation of messenger (use with messenger.getBinder())
     * @throws RemoteException
     */
    @Override
    public void registerMessenger(IBinder messenger) throws RemoteException {
        LOGD(TAG, "registerMessenger()");
        SilkCastService service = mService.get();
        if (service != null) {
            service.mMessengers.add(new Messenger(messenger));
        }
    }

    /**
     * unRegister a remote messenger
     * @param messenger binder representation of messenger (use with messenger.getBinder())
     * @throws RemoteException
     */
    @Override
    public void unregisterMessenger(IBinder messenger) throws RemoteException {
        LOGD(TAG, "unregisterMessenger()");
        SilkCastService service = mService.get();
        if (service != null) {
            // This seems to work ok since the IBinder will make the new messenger point to the same object
            service.mMessengers.remove(new Messenger(messenger));
            LOGD(TAG, "unregisterMessenger() " + service.mMessengers.size() + " messengers remain");
        }
    }
}
