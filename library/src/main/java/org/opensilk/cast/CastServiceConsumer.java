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

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.common.ConnectionResult;

import org.opensilk.cast.callbacks.IMediaCastConsumer;
import org.opensilk.cast.util.CastPreferences;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import static org.opensilk.cast.CastMessage.CAST_APPLICATION_CONNECTED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_CONNECTION_FAILED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_DISCONNECTED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STATUS_CHANGED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STOPPED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STOP_FAILED;
import static org.opensilk.cast.CastMessage.CAST_AVAILABILITY_CHANGED;
import static org.opensilk.cast.CastMessage.CAST_CONNECTED;
import static org.opensilk.cast.CastMessage.CAST_CONNECTION_FAILED;
import static org.opensilk.cast.CastMessage.CAST_CONNECTION_SUSPENDED;
import static org.opensilk.cast.CastMessage.CAST_CONNECTIVITY_RECOVERED;
import static org.opensilk.cast.CastMessage.CAST_DATA_MESSAGE_RECEIVED;
import static org.opensilk.cast.CastMessage.CAST_DATA_MESSAGE_SEND_FAILED;
import static org.opensilk.cast.CastMessage.CAST_DEVICE_DETECTED;
import static org.opensilk.cast.CastMessage.CAST_DISCONNECTED;
import static org.opensilk.cast.CastMessage.CAST_FAILED;
import static org.opensilk.cast.CastMessage.CAST_REMOTE_MEDIA_PLAYER_META_UPDATED;
import static org.opensilk.cast.CastMessage.CAST_REMOTE_MEDIA_PLAYER_STATUS_UPDATED;
import static org.opensilk.cast.CastMessage.CAST_REMOVED_NAMESPACE;
import static org.opensilk.cast.CastMessage.CAST_VOLUME_CHANGED;

/**
 * Created by drew on 3/15/14.
 */
public class CastServiceConsumer implements IMediaCastConsumer {
    private final WeakReference<SilkCastService> mService;

    CastServiceConsumer(SilkCastService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId, boolean wasLaunched) {
        sendMessage(CAST_APPLICATION_CONNECTED);
    }

    @Override
    public boolean onApplicationConnectionFailed(int errorCode) {
        sendMessage(CAST_APPLICATION_CONNECTION_FAILED, errorCode);
        return false;
    }

    @Override
    public void onApplicationStopped() {
        sendMessage(CAST_APPLICATION_STOPPED);
    }

    @Override
    public void onApplicationStopFailed(int errorCode) {
        sendMessage(CAST_APPLICATION_STOP_FAILED, errorCode);
    }

    @Override
    public void onApplicationStatusChanged(String appStatus) {
        Message msg = Message.obtain(null, CAST_APPLICATION_STATUS_CHANGED);
        Bundle b = new Bundle(1);
        b.putString("text", appStatus);
        msg.setData(b);
        sendMessage(msg);
    }

    @Override
    public void onVolumeChanged(double value, boolean isMute) {
        Context ctx = mService.get();
        if (ctx != null) {
            CastPreferences.putFloat(ctx, CastPreferences.KEY_REMOTE_VOLUME, (float)value);
        }
        sendMessage(CAST_VOLUME_CHANGED);
    }

    @Override
    public void onApplicationDisconnected(int errorCode) {
        sendMessage(CAST_APPLICATION_DISCONNECTED, errorCode);
    }

    @Override
    public void onRemoteMediaPlayerMetadataUpdated() {
        sendMessage(CAST_REMOTE_MEDIA_PLAYER_META_UPDATED);
    }

    @Override
    public void onRemoteMediaPlayerStatusUpdated() {
        sendMessage(CAST_REMOTE_MEDIA_PLAYER_STATUS_UPDATED);
    }

    @Override
    public void onRemovedNamespace() {
        sendMessage(CAST_REMOVED_NAMESPACE);
    }

    @Override
    public void onDataMessageSendFailed(int errorCode) {
        sendMessage(CAST_DATA_MESSAGE_SEND_FAILED, errorCode);
    }

    @Override
    public void onDataMessageReceived(String message) {
        Message msg = Message.obtain(null, CAST_DATA_MESSAGE_RECEIVED);
        Bundle b = new Bundle(1);
        b.putString("text", message);
        msg.setData(b);
        sendMessage(msg);
    }

    @Override
    public void onConnected() {
        sendMessage(CAST_CONNECTED);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        sendMessage(CAST_CONNECTION_SUSPENDED, cause);
    }

    @Override
    public void onDisconnected() {
        sendMessage(CAST_DISCONNECTED);
    }

    @Override
    public boolean onConnectionFailed(ConnectionResult result) {
        sendMessage(CAST_CONNECTION_FAILED, result.getErrorCode());
        return false;
    }

    @Override
    public void onCastDeviceDetected(MediaRouter.RouteInfo info) {
        sendMessage(CAST_DEVICE_DETECTED);
    }

    @Override
    public void onCastAvailabilityChanged(boolean castPresent) {
        sendMessage(CAST_AVAILABILITY_CHANGED, castPresent ? 1 : 0);
    }

    @Override
    public void onConnectivityRecovered() {
        sendMessage(CAST_CONNECTIVITY_RECOVERED);
    }

    @Override
    public void onFailed(int resourceId, int statusCode) {
        Message msg = Message.obtain(null, CAST_FAILED, resourceId, statusCode);
        sendMessage(msg);
    }

    private void sendMessage(int what) {
        Message msg = Message.obtain(null, what);
        sendMessage(msg);
    }

    private void sendMessage(int what, int arg1) {
        Message msg = Message.obtain(null, what, arg1, 0);
        sendMessage(msg);
    }

    private void sendMessage(Message msg) {
        SilkCastService service = mService.get();
        if (service != null) {
            synchronized (service.mMessengerLock) {
                Iterator<Messenger> ii = service.mMessengers.iterator();
                while (ii.hasNext()) {
                    Messenger m = ii.next();
                    if (m != null) {
                        try {
                            m.send(Message.obtain(msg));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            ii.remove();
                        }
                    }
                }
            }
        }
    }

}
