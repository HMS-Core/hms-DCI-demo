/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.codelab.dcidemo.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

/**
 *  HMS Push Helper class.
 *
 * @since 2021-06-02
 */
public class HmsPushHelper {
    /**
     * HMS push token.
     */
    public volatile static String PUSH_TOKEN;

    private static final String TAG = "HmsPushHelper";

    private HmsPushHelper() {
    }

    /**
     * Get HMS pushToken.
     *
     * @param context Context
     */
    public static synchronized void getToken(@NonNull final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
                    if (TextUtils.isEmpty(token)) {
                        Log.e(TAG, "token is empty.");
                        return;
                    }
                    setPushToken(token);
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed,statusCode = " + e.getStatusCode());
                }
            }
        }).start();
    }

    /**
     * Setting HMS pushToken.
     *
     * @param pushToken pushToken
     */
    public static void setPushToken(String pushToken) {
        PUSH_TOKEN = pushToken;
    }
}