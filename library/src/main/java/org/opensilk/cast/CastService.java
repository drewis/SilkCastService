package org.opensilk.cast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by drew on 3/15/14.
 */
public class CastService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
