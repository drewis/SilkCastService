/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensilk.cast.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.images.WebImage;

import java.util.ArrayList;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    private static final String TAG = LogUtils.makeLogTag(Utils.class);
    private static final String KEY_IMAGES = "images";
    private static final String KEY_URL = "movie-urls";
    private static final String KEY_CONTENT_TYPE = "content-type";

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     * 
     * @param millis
     * @return
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    /**
     * Returns the URL of an image for the {@link MediaInformation} at the given level. Level should
     * be a number between 0 and <code>n - 1</code> where <code>n
     * </code> is the number of images for that given item.
     * 
     * @param info
     * @param level
     * @return
     */
    public static String getImageUrl(MediaInfo info, int level) {
        MediaMetadata mm = info.getMetadata();
        if (null != mm && null != mm.getImages() && mm.getImages().size() > level) {
            return mm.getImages().get(level).getUrl().toString();
        }
        return null;
    }

    /**
     * Saves a string value under the provided key in the preference manager. If <code>value</code>
     * is <code>null</code>, then the provided key will be removed from the preferences.
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void saveStringToPreference(Context context, String key, String value) {
        CastPreferences.putString(context, key, value);
    }

    /**
     * Saves a float value under the provided key in the preference manager. If <code>value</code>
     * is <code>Float.MIN_VALUE</code>, then the provided key will be removed from the preferences.
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void saveFloatToPreference(Context context, String key, float value) {
        CastPreferences.putFloat(context, key, value);
    }

    /**
     * Saves boolean to cast shared prefs
     * @param context
     * @param key
     * @param value
     */
    public static void saveBooleanToPreference(Context context, String key, boolean value) {
        CastPreferences.putBoolean(context, key, value);
    }

    /**
     * Retrieves a String value from preference manager. If no such key exists, it will return
     * <code>null</code>.
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getStringFromPreference(Context context, String key) {
        return CastPreferences.getString(context, key);
    }

    /**
     * Retrieves a float value from preference manager. If no such key exists, it will return
     * <code>Float.MIN_VALUE</code>.
     * 
     * @param context
     * @param key
     * @return
     */
    public static float getFloatFromPreference(Context context, String key) {
        return CastPreferences.getFloat(context, key, Float.MIN_VALUE);
    }

    /**
     * Retrieves a boolean value from preference manager. If no such key exists, it will return the
     * value provided as <code>defaultValue</code>
     * 
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanFromPreference(Context context, String key, boolean defaultValue) {
        return CastPreferences.getBoolean(context, key, defaultValue);
    }

    /**
     * A utility method to validate that the appropriate version of the Google Play Services is
     * available on the device. If not, it will open a dialog to address the issue. The dialog
     * displays a localized message about the error and upon user confirmation (by tapping on
     * dialog) will direct them to the Play Store if Google Play services is out of date or missing,
     * or to system settings if Google Play services is disabled on the device.
     * 
     * @param activity
     * @return
     */
    public static boolean checkGooglePlaySevices(final Activity activity) {
        final int googlePlayServicesCheck = GooglePlayServicesUtil.isGooglePlayServicesAvailable(
                activity);
        switch (googlePlayServicesCheck) {
            case ConnectionResult.SUCCESS:
                return true;
            default:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCheck,
                        activity, 0);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
                dialog.show();
        }
        return false;
    }

    /**
     * Builds and returns a {@link Bundle} which contains a select subset of data in the
     * {@link MediaInfo}. Since {@link MediaInfo} is not {@link Parcelable}, one can use this
     * container bundle to pass around from one activity to another.
     * 
     * @see <code>toMediaInfo()</code>
     * @param info
     * @return
     */
    public static Bundle fromMediaInfo(MediaInfo info) {
        if (null == info) {
            return null;
        }

        MediaMetadata md = info.getMetadata();
        Bundle wrapper = new Bundle();
        wrapper.putString(MediaMetadata.KEY_TITLE, md.getString(MediaMetadata.KEY_TITLE));
        wrapper.putString(MediaMetadata.KEY_SUBTITLE, md.getString(MediaMetadata.KEY_SUBTITLE));
        wrapper.putString(KEY_URL, info.getContentId());
        wrapper.putString(MediaMetadata.KEY_STUDIO, md.getString(MediaMetadata.KEY_STUDIO));
        wrapper.putString(KEY_CONTENT_TYPE, info.getContentType());
        if (null != md.getImages()) {
            ArrayList<String> urls = new ArrayList<String>();
            for (WebImage img : md.getImages()) {
                urls.add(img.getUrl().toString());
            }
            wrapper.putStringArrayList(KEY_IMAGES, urls);
        }

        return wrapper;
    }

    /**
     * Builds and returns a {@link MediaInfo} that was wrapped in a {@link Bundle} by
     * <code>fromMediaInfo</code>.
     * 
     * @see <code>fromMediaInfo()</code>
     * @param wrapper
     * @return
     */
    public static MediaInfo toMediaInfo(Bundle wrapper) {
        if (null == wrapper) {
            return null;
        }

        MediaMetadata metaData = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        metaData.putString(MediaMetadata.KEY_SUBTITLE,
                wrapper.getString(MediaMetadata.KEY_SUBTITLE));
        metaData.putString(MediaMetadata.KEY_TITLE, wrapper.getString(MediaMetadata.KEY_TITLE));
        metaData.putString(MediaMetadata.KEY_STUDIO, wrapper.getString(MediaMetadata.KEY_STUDIO));
        ArrayList<String> images = wrapper.getStringArrayList(KEY_IMAGES);
        if (null != images && !images.isEmpty()) {
            for (String url : images) {
                Uri uri = Uri.parse(url);
                metaData.addImage(new WebImage(uri));
            }
        }
        return new MediaInfo.Builder(wrapper.getString(KEY_URL))
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(wrapper.getString(KEY_CONTENT_TYPE))
                .setMetadata(metaData)
                .build();
    }
}
