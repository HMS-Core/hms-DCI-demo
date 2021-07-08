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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.huawei.codelab.dcidemo.R;
import com.huawei.codelab.dcidemo.utils.DataUtils;
import com.huawei.codelab.dcidemo.utils.FileUtils;
import com.huawei.codelab.dcidemo.utils.PermissionsUtils;
import com.huawei.hms.dci.entity.ParamsInfoEntity;
import com.huawei.hms.dci.entity.WorkDciInfoEntity;
import com.huawei.hms.dci.function.HwDciClientCallBack;
import com.huawei.hms.dci.function.HwDciException;
import com.huawei.hms.dci.function.HwDciPublicClient;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Register copyright for photographic work class.
 *
 * @since 2021-06-02
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegistrationActivity";

    private static final int PICTURE_REQUEST_CODE = 1002;

    private String mWorkId;

    private DealImageHandler mDealImageHandler;

    private String mDciCode;

    private String selectFilePath;

    private ImageView mImageView;

    /**
     * Start DCI Work Registration Processing class.
     *
     * @param context Activity
     */
    public static void start(Activity context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mDealImageHandler = new DealImageHandler(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.about_registration));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initData(getIntent());
        permissionsUtils = PermissionsUtils.getInstance();
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_dci_logo);
        findViewById(R.id.btn_select_picture).setOnClickListener(this);
        findViewById(R.id.btn_dci_registration).setOnClickListener(this);
        findViewById(R.id.btn_query_result).setOnClickListener(this);
        findViewById(R.id.btn_add_dci_watermark).setOnClickListener(this);
        findViewById(R.id.btn_revoke_copyright).setOnClickListener(this);
    }

    private void initData(Intent intent) {
        if (intent == null) {
            return;
        }
        // If you integrate the HMS Push capability and choose Obtaining DCI Copyright User Information or Registering a DCI Copyright User,the hmsPushToken parameter is transferred.
        // When there is a result notification for DCI registration, the notification bar will display the result notification of DCI registration.
        // Click DCI registration result notification. The activity for configuring the corresponding scheme protocol is displayed (for details, see AndroidManifest.xml).
        // You can obtain the URI from the intent and parse the DCI work ID, DCI registration status, and DCI code.
        Uri data = intent.getData();
        if (data != null) {
            // state DCI Registered Work Status：1:success 2:fail 3:revoked 4:manual review
            String workId = data.getQueryParameter("workId");
            String state = data.getQueryParameter("state");
            String dciCode = data.getQueryParameter("dciCode");
            Log.e(TAG, "DCI Registered Work ID = " + workId);
            Log.e(TAG, "The status of the work registered by DCI = " + state);
            Log.e(TAG, "DCI code = " + dciCode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_picture:
                // select photographic work
                selectWorkPic();
                break;
            case R.id.btn_dci_registration:
                // register
                applyDciCode();
                break;
            case R.id.btn_query_result:
                // view registration information
                queryDciResult();
                break;
            case R.id.btn_revoke_copyright:
                // deregister
                revokeDciCode();
                break;
            case R.id.btn_add_dci_watermark:
                // add DCI icon for registered photographic work
                addDciWatermark();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissDialog();
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == PICTURE_REQUEST_CODE) {
            String filePath = FileUtils.getRealFilePath(this, data.getData());
            if (TextUtils.isEmpty(filePath)) {
                Log.e(TAG, "onActivityResult filePath is null");
                return;
            }
            selectFilePath = filePath;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectFilePath, options);
            options.inSampleSize = calculateInSampleSize(options, 800, 800);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(selectFilePath, options);
            mImageView.setImageBitmap(bitmap);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth && height > reqHeight) {
            inSampleSize = Math.min(Math.round((float) width / (float) reqWidth), Math.round((float) height / (float) reqHeight));
        }
        return inSampleSize;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }

    private void selectWorkPic() {
        permissionsUtils.checkPermissions(
                this,
                PermissionsUtils.getPermissions(),
                new PermissionsUtils.IPermissionsResult() {
                    @Override
                    public void passPermissions() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICTURE_REQUEST_CODE);
                    }

                    @Override
                    public void forbidPermissions(List<String> deniedPermissions) {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.please_permission_first), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyDciCode() {
        if (TextUtils.isEmpty(selectFilePath)) {
            Toast.makeText(this, getString(R.string.please_select_picture_first), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setDciUid(DataUtils.getDciUid());
        try {
            // location address Fill in the actual information
            HwDciPublicClient.applyDciCode(
                    paramsInfoEntity,
                    selectFilePath,
                    "北京市",
                    System.currentTimeMillis(),
                    new HwDciClientCallBack<String>() {
                        @Override
                        public void onSuccess(String workId) {
                            Log.e(TAG, "DCI Registered Work ID = " + workId);
                            mWorkId = workId;
                            dismissDialog();
                            Toast.makeText(RegistrationActivity.this, getString(R.string.uploading_registered_work_succeeded), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            dismissDialog();
                            disposeError(code, msg);
                        }
                    });
        } catch (HwDciException e) {
            dismissDialog();
            Log.e(TAG, e.getMessage());
        }
    }

    private void queryDciResult() {
        if (TextUtils.isEmpty(mWorkId)) {
            Toast.makeText(this, getString(R.string.please_registration_work_first), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setDciUid(DataUtils.getDciUid());
        paramsInfoEntity.setWorkId(mWorkId);
        try {
            HwDciPublicClient.queryWorkDciInfo(paramsInfoEntity, new HwDciClientCallBack<WorkDciInfoEntity>() {
                @Override
                public void onSuccess(WorkDciInfoEntity result) {
                    dismissDialog();
                    if (result == null) {
                        return;
                    }
                    // 0:dealing 1:success 2:fail
                    if (result.getRegistrationStatus() == 1) {
                        mDciCode = result.getDciCode();
                        Log.e(TAG, "DCI code = " + mDciCode);
                        Toast.makeText(RegistrationActivity.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                    } else if (result.getRegistrationStatus() == 0) {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.registration_processing), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.registration_failed) + ":" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail(int code, String msg) {
                    dismissDialog();
                    disposeError(code, msg);
                }
            });
        } catch (HwDciException ex) {
            dismissDialog();
            Log.e(TAG, ex.getMessage());
        }
    }

    private void revokeDciCode() {
        if (TextUtils.isEmpty(mDciCode)) {
            Toast.makeText(this, getString(R.string.please_query_dci_registration_result), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setDciUid(DataUtils.getDciUid());
        try {
            HwDciPublicClient.revokeDciCode(paramsInfoEntity, mDciCode, new HwDciClientCallBack<Void>() {
                @Override
                public void onSuccess(Void v) {
                    dismissDialog();
                    Toast.makeText(RegistrationActivity.this, getString(R.string.revoke_dci_code_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(int code, String msg) {
                    dismissDialog();
                    disposeError(code, msg);
                }
            });
        } catch (HwDciException e) {
            dismissDialog();
            Log.e(TAG, e.getMessage());
        }
    }

    private void disposeError(int code, String msg) {
        // deal error logic
        Log.e(TAG, "code = " + code + ",msg = " + msg);
    }

    private void addDciWatermark() {
        if (TextUtils.isEmpty(selectFilePath)) {
            Toast.makeText(this, getString(R.string.please_select_picture_first), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        try {
            HwDciPublicClient.addDciWatermark(
                    selectFilePath,
                    new HwDciClientCallBack<String>() {
                        @Override
                        public void onSuccess(String strBitmap) {
                            dismissDialog();
                            stringToBitmap(strBitmap);
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

    private void stringToBitmap(String string) {
        new Thread(() -> {
            Bitmap bitmap;
            try {
                byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
                if (bitmapArray != null) {
                    bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    mDealImageHandler.sendMessage(message);
                }
            } catch (Exception e) {
                mDealImageHandler.sendEmptyMessage(0);
                Log.e(TAG, "base64 to bitmap exception");
            }
        }).start();
    }

    /**
     * Handler class.
     *
     * @since 2021-06-02
     */
    private static class DealImageHandler extends Handler {
        private WeakReference<RegistrationActivity> mReference;

        public DealImageHandler(RegistrationActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mReference == null) {
                return;
            }
            RegistrationActivity preDealActivity = mReference.get();
            if (preDealActivity != null) {
                preDealActivity.dismissDialog();
                if (msg.obj instanceof Bitmap) {
                    preDealActivity.mImageView.setImageBitmap((Bitmap) msg.obj);
                }
            }
        }
    }
}
