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

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

/**
 * HMS Push Service class.
 *
 * @since 2021-06-02
 */
public class HwPushMsgService extends HmsMessageService {
    private static final String TAG = "HwPushMsgService";

    @Override
    public void onNewToken(String token, Bundle bundle) {
        if (!TextUtils.isEmpty(token)) {
            Log.e(TAG, "onNewToken: get token success " + token);
            HmsPushHelper.setPushToken(token);
        }
    }
}
