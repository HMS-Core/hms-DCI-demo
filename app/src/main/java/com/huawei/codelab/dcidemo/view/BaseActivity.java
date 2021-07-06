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

package com.huawei.codelab.dcidemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.codelab.dcidemo.dialog.AppLoadingDialog;
import com.huawei.codelab.dcidemo.utils.PermissionsUtils;

/**
 * Base Activity class.
 *
 * @since 2021-06-02
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Permission Utils.
     */
    protected PermissionsUtils permissionsUtils;

    private AppLoadingDialog mLoadingDialog;

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsUtils != null) {
            permissionsUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    /**
     * Show loading.
     */
    protected void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new AppLoadingDialog(this);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * Hide loading.
     */
    protected void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
