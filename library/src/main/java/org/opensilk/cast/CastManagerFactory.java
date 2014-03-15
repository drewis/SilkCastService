package org.opensilk.cast;

import android.content.Context;

/**
 * Created by drew on 2/19/14.
 */
public class CastManagerFactory {
    private static MediaCastManager sCastManager = null;

    private CastManagerFactory() {
    }

    public static MediaCastManager getCastManager(Context context) {
        if (sCastManager == null) {
            sCastManager = MediaCastManager.initialize(context, "CAST_APPLICATION_ID", null, null);
            if (BuildConfig.DEBUG) {
                sCastManager.enableFeatures(MediaCastManager.FEATURE_DEBUGGING);
            }
            // We are streaming /from/ the device so it needs to exit
            sCastManager.setStopOnDisconnect(true);
        }
        return sCastManager;
    }
}
