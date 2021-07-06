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

package com.huawei.codelab.dcidemo.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.huawei.codelab.dcidemo.BuildConfig;
import com.huawei.codelab.dcidemo.DciDemoApplication;
import com.huawei.codelab.dcidemo.R;
import com.huawei.hms.dci.entity.ParamsInfoEntity;

/**
 * Data Utils class.
 *
 * @since 2021-06-02
 */
public class DataUtils {
    private static final String APP_ID = BuildConfig.APPID;
    private static String HMS_TOKEN;
    private static String HMS_OPEN_ID;
    private static String DCI_UID;

    public static String getHmsToken() {
        return HMS_TOKEN;
    }

    public static void setHmsToken(String hmsToken) {
        HMS_TOKEN = hmsToken;
    }

    public static String getHmsOpenId() {
        return HMS_OPEN_ID;
    }

    public static void setHmsOpenId(String hmsOpenId) {
        HMS_OPEN_ID = hmsOpenId;
    }

    public static String getDciUid() {
        return DCI_UID;
    }

    public static void setDciUid(String dciUid) {
        DCI_UID = dciUid;
    }

    /**
     * Check whether the hms parameter is empty.
     *
     * @return true if the parameter is empty.
     */
    public static boolean checkHms() {
        if (TextUtils.isEmpty(DataUtils.getHmsToken()) || TextUtils.isEmpty(DataUtils.getHmsOpenId())) {
            Toast.makeText(DciDemoApplication.getInstance(), DciDemoApplication.getInstance().getString(R.string.please_login_hms_first), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Encapsulates DCI Kit general parameters.
     *
     * @return paramsInfoEntity
     */
    public static ParamsInfoEntity getCommonParamsInfoEntity() {
        ParamsInfoEntity paramsInfoEntity = new ParamsInfoEntity();
        paramsInfoEntity.setHmsToken(HMS_TOKEN);
        paramsInfoEntity.setHmsAppId(APP_ID);
        paramsInfoEntity.setHmsOpenId(HMS_OPEN_ID);
        return paramsInfoEntity;
    }
}
