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
import android.os.RemoteException;

import java.lang.ref.WeakReference;

/**
 * Implementation for remote processes to access CastService
 *
 * Created by drew on 3/15/14.
 */
public class CastServiceImpl extends ICastService.Stub {

    private final WeakReference<SilkCastService> mService;
    private final ICastManager mCastManager;

    CastServiceImpl(SilkCastService service) {
        mService = new WeakReference<>(service);
        mCastManager = new CastManagerImpl(service);
    }

    /**
     * @return CastService messenger
     *         Clients can then register a messenger to receive callbacks with
     * @throws RemoteException
     */
    @Override
    public IBinder getMessenger() throws RemoteException {
        SilkCastService service = mService.get();
        if (service != null) {
            return service.mCallbackMessenger.getBinder();
        }
        return null;
    }

    /**
     * @return proxy implementation of ICastManager
     * @throws RemoteException
     */
    @Override
    public ICastManager getCastManager() throws RemoteException {
        return mCastManager;
    }
}
