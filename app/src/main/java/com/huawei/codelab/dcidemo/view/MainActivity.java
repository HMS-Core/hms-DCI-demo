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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huawei.codelab.dcidemo.ErrorCode;
import com.huawei.codelab.dcidemo.R;
import com.huawei.codelab.dcidemo.push.HmsPushHelper;
import com.huawei.codelab.dcidemo.utils.DataUtils;
import com.huawei.codelab.dcidemo.utils.HmsLoginUtils;
import com.huawei.codelab.dcidemo.utils.PermissionsUtils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.dci.entity.AccountInfoEntity;
import com.huawei.hms.dci.entity.ParamsInfoEntity;
import com.huawei.hms.dci.function.HwDciClientCallBack;
import com.huawei.hms.dci.function.HwDciConstant;
import com.huawei.hms.dci.function.HwDciException;
import com.huawei.hms.dci.function.HwDciPublicClient;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.result.AuthAccount;

import java.util.List;

/**
 * Main Activity class.
 *
 * @since 2021-06-02
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    /**
     * HMS Account login success code.
     */
    public static final int HMS_ACCOUNT_SUCCESS_CODE = 1000;

    private static final int CODE_GET_DCI_ACCOUNT = 1001;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();
        HmsPushHelper.getToken(this);
        permissionsUtils = PermissionsUtils.getInstance();
    }

    private void initListener() {
        findViewById(R.id.btn_login_hms_account).setOnClickListener(this);
        findViewById(R.id.btn_get_dci_account).setOnClickListener(this);
        findViewById(R.id.btn_register_dci_account).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
        findViewById(R.id.btn_close_dci_account).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_hms_account:
                // hms account login
                loginHmsAccount();
                break;
            case R.id.btn_register_dci_account:
                // register as DCI user
                registerDciAccount();
                break;
            case R.id.btn_close_dci_account:
                // show deregister DCI user dialog
                showCloseDciAccountDialog();
                break;
            case R.id.btn_get_dci_account:
                // view registration information
                getDciAccount();
                break;
            case R.id.btn_registration:
                if (checkHmsAndDciInActive()) {
                    return;
                }
                // register digital work copyright
                RegistrationActivity.start(this);
                break;
            default:
                break;
        }
    }

    private void loginHmsAccount() {
        showLoading();
        HmsLoginUtils.loginHmsAccount(
                this,
                new HmsLoginUtils.LoginHmsAccountCallBack() {
                    @Override
                    public void hmsLoginSuccess(String hmsToken, String hmsOpenId) {
                        DataUtils.setHmsToken(hmsToken);
                        DataUtils.setHmsOpenId(hmsOpenId);
                        Toast.makeText(MainActivity.this, getString(R.string.hms_login_success), Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }

                    @Override
                    public void hmsLoginFail(String msg) {
                        dismissDialog();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDciAccount() {
        if (DataUtils.checkHms()) {
            return;
        }
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        // If the DCI Kit needs to register result notifications, the app needs to integrate the HMS push function and transfer the pushToken.
        // If this parameter is not required, do not transfer this parameter.
        paramsInfoEntity.setHmsPushToken(HmsPushHelper.PUSH_TOKEN);
        try {
            HwDciPublicClient.getDciAccount(
                    paramsInfoEntity,
                    new HwDciClientCallBack<AccountInfoEntity>() {
                        @Override
                        public void onSuccess(AccountInfoEntity accountInfoEntity) {
                            dismissDialog();
                            Log.e(TAG, "DCI copyRight user ID = " + accountInfoEntity.getUserId());
                            // save DCI Uid
                            DataUtils.setDciUid(accountInfoEntity.getUserId());
                            Toast.makeText(MainActivity.this, getString(R.string.succeeded_get_dci_account), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(int code, String s) {
                            dismissDialog();
                            Log.e(TAG, "code = " + code + " ,message = " + s);
                            if (code == ErrorCode.NOT_REGISTER) {
                                // The current hms account has not been registered as a DCI copyright user.
                                Toast.makeText(MainActivity.this, getString(R.string.current_not_register_dci_account), Toast.LENGTH_SHORT).show();
                            } else if (code == ErrorCode.HW_TOKEN_OR_UID_NOT_USED) {
                                // hms token or openId unavailable
                                Toast.makeText(MainActivity.this, getString(R.string.please_login_hms_again), Toast.LENGTH_SHORT).show();
                            } else {
                                // other error
                            }
                        }
                    });
        } catch (HwDciException e) {
            dismissDialog();
            Log.e(TAG, e.getMessage());
        }
    }

    private void registerDciAccount() {
        if (DataUtils.checkHms()) {
            return;
        }
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setHmsPushToken(HmsPushHelper.PUSH_TOKEN);
        if (permissionsUtils == null) {
            return;
        }
        permissionsUtils.checkPermissions(
                MainActivity.this,
                PermissionsUtils.getPermissions(),
                new PermissionsUtils.IPermissionsResult() {
                    @Override
                    public void passPermissions() {
                        try {
                            HwDciPublicClient.registerDciAccount(MainActivity.this, paramsInfoEntity, CODE_GET_DCI_ACCOUNT);
                        } catch (HwDciException ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    }

                    @Override
                    public void forbidPermissions(List<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, getString(R.string.file_access_denied), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCloseDciAccountDialog() {
        if (checkHmsAndDciInActive()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.deregister_dci_account_whether)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeAccount();
            }
        }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show();
    }

    /**
     * Close DCI copyRight account.
     */
    private void closeAccount() {
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setDciUid(DataUtils.getDciUid());
        try {
            HwDciPublicClient.closeDciAccount(
                    paramsInfoEntity,
                    new HwDciClientCallBack<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            dismissDialog();
                            if (aBoolean == null) {
                                return;
                            }
                            if (aBoolean) {
                                Toast.makeText(MainActivity.this, getString(R.string.close_dci_account_success), Toast.LENGTH_SHORT).show();
                                DataUtils.setDciUid(null);
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.close_dci_account_failed), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            dismissDialog();
                            Log.e(TAG, "code = " + code + ",msg = " + msg);
                        }
                    });
        } catch (HwDciException ex) {
            dismissDialog();
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissDialog();
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == HMS_ACCOUNT_SUCCESS_CODE) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask == null) {
                Log.e(TAG, "onActivityResult authAccountTask is null");
                return;
            }
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                if (authAccount != null) {
                    DataUtils.setHmsToken(authAccount.getAccessToken());
                    DataUtils.setHmsOpenId(authAccount.getOpenId());
                    Toast.makeText(MainActivity.this, getString(R.string.hms_login_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "onActivityResult authAccountTask exception is " + authAccountTask.getException());
            }
        } else if (requestCode == CODE_GET_DCI_ACCOUNT) {
            // DCI user is registered successfully
            if (!data.hasExtra(HwDciConstant.DCI_REGISTER_RESULT_CODE)) {
                return;
            }
            int code = data.getIntExtra(HwDciConstant.DCI_REGISTER_RESULT_CODE, 0);
            if (code == ErrorCode.GET_DCI_ACCOUNT_SUCCESS_CODE) {
                AccountInfoEntity accountInfoEntity = data.getParcelableExtra(HwDciConstant.DCI_ACCOUNT_INFO_KEY);
                if (accountInfoEntity != null) {
                    Log.e(TAG, "DCI copyRight user ID = " + accountInfoEntity.getUserId());
                    DataUtils.setDciUid(accountInfoEntity.getUserId());
                }
                Toast.makeText(MainActivity.this, getString(R.string.succeeded_get_dci_account), Toast.LENGTH_SHORT).show();
            } else {
                onActivityResultError(code);
            }
        }
    }

    private void onActivityResultError(int code) {
        switch (code) {
            case ErrorCode.REAL_NAME_FAIL_AS_FORCIBLE_EXIT:
                Log.e(TAG, "Real-Name Authentication Failure Caused by Forcible Exit");
                break;
            case ErrorCode.HW_TOKEN_OR_UID_NOT_USED:
                Log.e(TAG, "Hms token or openId unavailable");
                Toast.makeText(this, getString(R.string.please_login_hms_again), Toast.LENGTH_SHORT).show();
                break;
            case ErrorCode.REAL_NAME_FAIL_AS_TOKEN_OR_OPENID_NULL:
                Log.e(TAG, "Hms token or openId is null");
                break;
            default:
                break;
        }
    }

    private boolean checkHmsAndDciInActive() {
        if (DataUtils.checkHms()) {
            return true;
        }
        if (TextUtils.isEmpty(DataUtils.getDciUid())) {
            Toast.makeText(this, getString(R.string.please_get_dci_account), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
