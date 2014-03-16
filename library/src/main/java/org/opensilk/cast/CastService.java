package org.opensilk.cast;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.common.ConnectionResult;

import org.opensilk.cast.callbacks.IMediaCastConsumer;
import org.opensilk.cast.manager.MediaCastManager;

import java.util.HashSet;
import java.util.Set;

import static org.opensilk.cast.CastMessage.CAST_APPLICATION_CONNECTED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_CONNECTION_FAILED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_DISCONNECTED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STATUS_CHANGED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STOPPED;
import static org.opensilk.cast.CastMessage.CAST_APPLICATION_STOP_FAILED;
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
public class CastService extends Service {

    /**
     * Action for same process binding
     */
    public static final String ACTION_BIND_LOCAL = "bind_local";

    /**
     * Action for remote process binding
     */
    public static final String ACTION_BIND_REMOTE = "bind_remote";

    /**
     * Local binder
     */
    CastServiceBinder mLocalBinder;

    /**
     * Remote binder
     */
    CastServiceImpl mRemoteBinder;

    /**
     * Remote messager listeners
     * Local clients should just register a CastConsumer directly with CastManager
     */
    Set<Messenger> mMessengers = new HashSet<>();

    /**
     * Local callback handler, will forward events to remote messengers
     */
    final MediaCastListener mCastManagerListener = new MediaCastListener();

    /**
     * Service reference to CastManager instance
     */
    MediaCastManager mCastManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION_BIND_LOCAL.equals(intent.getAction())) {
            return mLocalBinder;
        } else if (ACTION_BIND_REMOTE.equals(intent.getAction())) {
            return mRemoteBinder;
        }
        throw new RuntimeException("Action not defined");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRemoteBinder = new CastServiceImpl(this);
        mLocalBinder = new CastServiceBinder(this);
        mCastManager = MediaCastManager.initialize(getApplicationContext(),
                getApplicationContext().getString(R.string.cast_id), null, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        throw new RuntimeException("CastService is bind only");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRemoteBinder = null;
        mLocalBinder = null;
        mCastManager = null;
    }

    private void sendMessage(int what) {
        Message msg = Message.obtain(null, what);
        sendMessage(msg);
    }

    private void sendMessage(int what, int arg1) {
        Message msg = Message.obtain(null, what, arg1, 0);
        sendMessage(msg);
    }

    private void sendMessage(int what, String text) {
        Message msg = Message.obtain(null, what, 0, 0, text);
        sendMessage(msg);
    }

    private void sendMessage(Message msg) {
        for (Messenger m : mMessengers) {
            if (m != null) {
                try {
                    m.send(Message.obtain(msg));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaCastListener implements IMediaCastConsumer {
        @Override
        public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId, boolean wasLaunched) {
            sendMessage(CAST_APPLICATION_CONNECTED);
        }

        @Override
        public void onApplicationConnectionFailed(int errorCode) {
            sendMessage(CAST_APPLICATION_CONNECTION_FAILED, errorCode);
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
            sendMessage(CAST_APPLICATION_STATUS_CHANGED, appStatus);
        }

        @Override
        public void onVolumeChanged(double value, boolean isMute) {
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
            sendMessage(CAST_DATA_MESSAGE_RECEIVED, message);
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
        public void onConnectionFailed(ConnectionResult result) {
            sendMessage(CAST_CONNECTION_FAILED, result.getErrorCode());
        }

        @Override
        public void onCastDeviceDetected(MediaRouter.RouteInfo info) {
            sendMessage(CAST_DEVICE_DETECTED);
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
    }

    /**
     * Handle registering and unregistering remote messengers
     *
     * This seems a little round about, need to look into
     * passing a raw IBinder to add to mMessengers
     */

    public static final int MESSENGER_REGISTER = 1;
    public static final int MESSENGER_UNREGISTER = 2;

    final Messenger mCallbackMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSENGER_REGISTER:
                    mMessengers.add(msg.replyTo);
                    break;
                case MESSENGER_UNREGISTER:
                    mMessengers.remove(msg.replyTo);
                    break;
            }
        }
    });
}
