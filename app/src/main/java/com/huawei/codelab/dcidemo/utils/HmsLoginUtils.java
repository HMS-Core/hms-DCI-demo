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

import android.app.Activity;
import android.content.Intent;

import com.huawei.codelab.dcidemo.view.MainActivity;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

/**
 * HMS Account login Utils class.
 *
 * @since 2021-06-02
 */
public class HmsLoginUtils {
    /**
     * HMS Account login.
     *
     * @param activity Activity
     * @param loginHmsAccountCallBack HmsLoginCallBack
     */
    public static void loginHmsAccount(final Activity activity, final LoginHmsAccountCallBack loginHmsAccountCallBack) {
        AccountAuthParams authParams =
                new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                        .setAccessToken()
                        .setProfile()
                        .createParams();
        final AccountAuthService service = AccountAuthManager.getService(activity, authParams);
        Task<AuthAccount> mTask = service.silentSignIn();
        mTask.addOnSuccessListener(
                new OnSuccessListener<AuthAccount>() {
                    @Override
                    public void onSuccess(AuthAccount authAccount) {
                        if (loginHmsAccountCallBack != null && authAccount != null) {
                            loginHmsAccountCallBack.hmsLoginSuccess(authAccount.getAccessToken(), authAccount.getOpenId());
                        }
                    }
                });
        mTask.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception ex) {
                        if (ex instanceof ApiException) {
                            Intent signInIntent = service.getSignInIntent();
                            if (signInIntent != null) {
                                activity.startActivityForResult(signInIntent, MainActivity.HMS_ACCOUNT_SUCCESS_CODE);
                            }
                        } else {
                            if (loginHmsAccountCallBack != null) {
                                loginHmsAccountCallBack.hmsLoginFail(ex.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * HMS Account login callback.
     */
    public interface LoginHmsAccountCallBack {
        /**
         * HMS Account login success.
         *
         * @param hmsToken  token
         * @param hmsOpenId openId
         */
        void hmsLoginSuccess(String hmsToken, String hmsOpenId);

        /**
         * HMS Account login failed.
         *
         * @param msg error message
         */
        void hmsLoginFail(String msg);
    }
}
