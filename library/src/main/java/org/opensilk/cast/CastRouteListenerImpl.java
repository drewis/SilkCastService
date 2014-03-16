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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.google.android.gms.cast.CastDevice;

import org.opensilk.cast.exceptions.NoConnectionException;
import org.opensilk.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.opensilk.cast.util.LogUtils.LOGE;

/**
 * Proxy for remote activity to inform cast manager of route selection changes
 *
 * Created by drew on 3/15/14.
 */
public class CastRouteListenerImpl extends ICastRouteListener.Stub {

    private static final String TAG = "CastRouteListener";

    private WeakReference<CastService> mService;

    CastRouteListenerImpl(CastService service) {
        mService = new WeakReference<>(service);
    }

    /**
     * The user has selected a device, we won't receive a callback from
     * the mediarouter so the activity has told us to connect manually
     * @param castDevice bundle representation of cast device
     * @throws RemoteException
     */
    @Override
    public void onRouteSelected(Bundle castDevice) throws RemoteException {
        final CastDevice device = CastDevice.getFromBundle(castDevice);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    mService.get().mCastManager.selectDevice(device);
                } catch (NullPointerException ignored) {
                }
            }
        });
    }

    /**
     * The user has disconnected from the device, again we won't be receiving
     * a callback from the mediarouter so they have informed us to stop the app
     * @throws RemoteException
     */
    @Override
    public void onRouteUnselected() throws RemoteException {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    mService.get().mCastManager.stopApplication();
                } catch (IOException | TransientNetworkDisconnectionException | NoConnectionException e) {
                    LOGE(TAG, "Failed to stop application: " + e.getClass().getSimpleName() + " " + e.getMessage());
                } catch (NullPointerException ignored) {
                }
            }
        });
    }
}