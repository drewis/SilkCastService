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
import org.opensilk.cast.manager.BaseCastManager.ReconnectionStatus;
import org.opensilk.cast.util.LogUtils;

import java.lang.ref.WeakReference;

import static org.opensilk.cast.util.LogUtils.LOGE;

/**
 * Front end for remote activity to communicate with the cast mananger
 *
 * Created by drew on 3/15/14.
 */
public class CastManagerImpl extends ICastManager.Stub {
    private static final String TAG = LogUtils.makeLogTag("ICastManager");

    private WeakReference<SilkCastService> mService;
    private ICastRouteListener mCastRouteListener;

    CastManagerImpl(SilkCastService service) {
        mService = new WeakReference<>(service);
        mCastRouteListener = new CastRouteListenerImpl(service);
    }

    /**
     * Changes remote volume
     * @param increment
     * @return true if request was sent, false on error
     * @throws RemoteException
     */
    @Override
    public boolean changeVolume(double increment) throws RemoteException {
        SilkCastService service = mService.get();
        if (service != null) {
            try {
                service.mCastManager.incrementVolume(increment);
                return true;
            } catch (CastException|TransientNetworkDisconnectionException|NoConnectionException e) {
                LOGE(TAG, "Failed to change remote volume: " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * @return a ReconnectionStatus value (@{ReconnectionStatus.INACTIVE} on error)
     * @throws RemoteException
     */
    @Override
    public String getReconnectionStatus() throws RemoteException {
        ReconnectionStatus status = ReconnectionStatus.INACTIVE;
        SilkCastService service = mService.get();
        if (service != null) {
            status = service.mCastManager.getReconnectionStatus();
        }
        return status.toString();
    }

    /**
     * Sets Reconnection status
     * @param status a ReconnectionStatus value
     * @return true if request was sent, false on error
     * @throws RemoteException
     */
    @Override
    public boolean setReconnectionStatus(String status) throws RemoteException {
        SilkCastService service = mService.get();
        if (service != null) {
            try {
                service.mCastManager.setReconnectionStatus(ReconnectionStatus.valueOf(status));
                return true;
            } catch (Exception e) { //cant remember what it throws IllegalARgument??
                //fall
            }
        }
        return false;
    }

    /**
     * @return ICastRouteListener to notify CastManager of route selection changes
     * @throws RemoteException
     */
    @Override
    public ICastRouteListener getRouteListener() throws RemoteException {
        return mCastRouteListener;
    }


    @Override
    public boolean retryConnect() throws RemoteException {
        SilkCastService service = mService.get();
        if (service != null) {
            if (service.mCastManager.isConnected()) {
                service.mCastManager.onConnected(null);
            } else {
                service.mCastManager.reconnectSessionIfPossible(service, false, 3);
            }
            return true;
        }
        return false;
    }
}
