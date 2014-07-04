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

/**
 * CastConsumer callbacks that can be sent as messages to remote process
 *
 * Created by drew on 3/15/14.
 */
public class CastMessage {
    /** empty */
    public static final int CAST_CONNECTED                          = 0x00000001;
    /** arg1 contains error code */
    public static final int CAST_CONNECTION_SUSPENDED               = 0x00000002;
    /** empty */
    public static final int CAST_DISCONNECTED                       = 0x00000003;
    /** arg1 contains error code */
    public static final int CAST_CONNECTION_FAILED                  = 0x00000004;
    /** empty */
    public static final int CAST_DEVICE_DETECTED                    = 0x00000005;
    /** empty */
    public static final int CAST_CONNECTIVITY_RECOVERED             = 0x00000006;
    /** empty */
    public static final int CAST_APPLICATION_CONNECTED              = 0x00000007;
    /** arg1 contains error code */
    public static final int CAST_APPLICATION_CONNECTION_FAILED      = 0x00000008;
    /** empty */
    public static final int CAST_APPLICATION_STOPPED                = 0x00000009;
    /** arg1 contains error code */
    public static final int CAST_APPLICATION_STOP_FAILED            = 0x0000000A;
    /** Data().getString("text") contains status */
    public static final int CAST_APPLICATION_STATUS_CHANGED         = 0x0000000B;
    /** empty */
    public static final int CAST_VOLUME_CHANGED                     = 0x0000000C;
    /** arg1 contains error code */
    public static final int CAST_APPLICATION_DISCONNECTED           = 0x0000000D;
    /** empty */
    public static final int CAST_REMOTE_MEDIA_PLAYER_META_UPDATED   = 0x0000000E;
    /** empty */
    public static final int CAST_REMOTE_MEDIA_PLAYER_STATUS_UPDATED = 0x0000000F;
    /** empty */
    public static final int CAST_REMOVED_NAMESPACE                  = 0x00000010;
    /** arg1 contains error code */
    public static final int CAST_DATA_MESSAGE_SEND_FAILED           = 0x00000011;
    /** Data().getString("text") contains message */
    public static final int CAST_DATA_MESSAGE_RECEIVED              = 0x00000012;
    /** arg1 resource id of error string, arg2 error code */
    public static final int CAST_FAILED                             = 0x00000013;
    /** arg1 1 for true 0 for false */
    public static final int CAST_AVAILABILITY_CHANGED               = 0x00000014;
}
