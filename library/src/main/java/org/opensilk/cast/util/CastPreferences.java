/*
 * Copyright (c) 2014 OpenSilk Productions LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.opensilk.cast.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by drew on 7/26/14.
 */
public class CastPreferences {

    public static final String KEY_SESSION_ID = "session-id";
    public static final String KEY_APPLICATION_ID = "application-id";
    public static final String KEY_CAST_ACTIVITY_NAME = "cast-activity-name";
    public static final String KEY_CAST_CUSTOM_DATA_NAMESPACE = "cast-custom-data-namespace";
    public static final String KEY_VOLUME_INCREMENT = "volume-increment";
    public static final String KEY_ROUTE_ID = "route-id";
    public static final String KEY_REMOTE_VOLUME = "volume-remote";
    public static final String KEY_CAST_ENABLED = "pref_cast_enabled";

    private static final String PREF_FILE = "Cast";

    private static SharedPreferences prefs;

    private static SharedPreferences getPrefs(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_MULTI_PROCESS);
        }
        return prefs;
    }

    /**
     * Saves a string value under the provided key in the preference manager. If <code>value</code>
     * is <code>null</code>, then the provided key will be removed from the preferences.
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences pref = getPrefs(context);
        if (null == value) {
            // we want to remove
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putString(key, value).apply();
        }
    }

    /**
     * Saves a float value under the provided key in the preference manager. If <code>value</code>
     * is <code>Float.MIN_VALUE</code>, then the provided key will be removed from the preferences.
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences pref = getPrefs(context);
        if (Float.MIN_VALUE == value) {
            // we want to remove
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putFloat(key, value).apply();
        }

    }

    /**
     * Saves boolean to cast shared prefs
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences pref = getPrefs(context);
        pref.edit().putBoolean(key, value).apply();
    }

    /**
     * Retrieves a String value from preference manager. If no such key exists, it will return
     * <code>null</code>.
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences pref = getPrefs(context);
        return pref.getString(key, null);
    }

    /**
     * Retrieves a float value from preference manager.
     *
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences pref = getPrefs(context);
        return pref.getFloat(key, defaultValue);
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
    public static boolean getBoolean(Context context, String key,
                                                   boolean defaultValue) {
        SharedPreferences pref = getPrefs(context);
        return pref.getBoolean(key, defaultValue);
    }
}
