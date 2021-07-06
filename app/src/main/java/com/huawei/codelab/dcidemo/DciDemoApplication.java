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

package com.huawei.codelab.dcidemo;

import android.app.Application;

import com.huawei.hms.dci.function.HwDciPublicClient;


/**
 * DciDemoApplication class.
 *
 * @since 2021-06-02
 */
public class DciDemoApplication extends Application {
    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        // Init DCI Kit
        HwDciPublicClient.initApplication(this);
    }

    private static void setInstance(DciDemoApplication application) {
        sApplication = application;
    }

    /**
     * Get application Instance.
     *
     * @return Application
     */
    public static Application getInstance() {
        return sApplication;
    }
}
