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
package org.opensilk.cast.helpers;

import android.content.Context;
import android.os.Messenger;

import org.opensilk.cast.CastServiceBinder;
import org.opensilk.cast.ICastService;

/**
 * Created by drew on 3/15/14.
 */
abstract class BaseCastServiceManager {

    protected final Context mContext;
    protected CastServiceConnectionCallback mCallback;

    BaseCastServiceManager(Context context) {
        mContext = context;
    }

    public void setCallback(CastServiceConnectionCallback cb) {
        mCallback = cb;
    }

}
