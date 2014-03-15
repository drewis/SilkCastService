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

package org.opensilk.cast.callbacks;

/**
 * @see IBaseCastConsumer and IMediaCastConsumer
 *
 * Created by drew on 3/15/14.
 */
interface IMediaCastRemoteConsumer {
    /** IBaseCastConsumer Callbacks */
    //void onConnected();
    void onConnectionSuspended(int cause);
    void onDisconnected();
    //boolean onConnectionFailed(ConnectionResult result);
    //void onCastDeviceDetected(RouteInfo info);
    void onConnectivityRecovered();

    /** IMediaCastConsumer Callbacks */
    //void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId, boolean wasLaunched);
    boolean onApplicationConnectionFailed(int errorCode);
    //void onApplicationStopped();
    //void onApplicationStopFailed(int errorCode);
    //void onApplicationStatusChanged(String appStatus);
    //void onVolumeChanged(double value, boolean isMute);
    void onApplicationDisconnected(int errorCode);
    //void onRemoteMediaPlayerMetadataUpdated();
    //void onRemoteMediaPlayerStatusUpdated();
    //void onRemovedNamespace();
    //void onDataMessageSendFailed(int errorCode);
    //void onDataMessageReceived(String message);

    /** OnFailedListener Callbacks */
    void onFailed(int resourceId, int statusCode);
}
