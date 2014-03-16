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

import android.os.RemoteException;

import org.opensilk.cast.exceptions.CastException;
import org.opensilk.cast.exceptions.NoConnectionException;
import org.opensilk.cast.exceptions.TransientNetworkDisconnectionException;
import org.opensilk.cast.manager.ReconnectionStatus;

import java.lang.ref.WeakReference;

/**
 * Front end for remote activity to communicate with the cast mananger
 *
 * Created by drew on 3/15/14.
 */
public class CastManagerImpl extends ICastManager.Stub {

    private WeakReference<CastService> mService;
    private ICastRouteListener mCastRouteListener;

    CastManagerImpl(CastService service) {
        mService = new WeakReference<>(service);
        mCastRouteListener = new CastRouteListenerImpl(service);
    }

    /**
     * Changes remote volume
     * @param increment
     * @throws RemoteException
     */
    @Override
    public void changeVolume(double increment) throws RemoteException {
        try {
            mService.get().mCastManager.incrementVolume(increment);
        } catch (CastException|TransientNetworkDisconnectionException|NoConnectionException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) { }
    }

    /**
     * @return a ReconnectionStatus value
     * @throws RemoteException
     */
    @Override
    public int getReconnectionStatus() throws RemoteException {
        int status = ReconnectionStatus.INACTIVE;
        try {
            status = mService.get().mCastManager.getReconnectionStatus();
        } catch (NullPointerException ignored) { }
        return status;
    }

    /**
     * Sets Reconnection status
     * @param status a ReconnectionStatus value
     * @throws RemoteException
     */
    @Override
    public void setReconnectionStatus(int status) throws RemoteException {
        try {
            mService.get().mCastManager.setReconnectionStatus(status);
        } catch (NullPointerException ignored) { }
    }

    /**
     * @return ICastRouteListener to notify CastManager of route selection changes
     * @throws RemoteException
     */
    @Override
    public ICastRouteListener getRouteListener() throws RemoteException {
        return mCastRouteListener;
    }

}
