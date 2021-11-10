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
import android.os.ParcelFileDescriptor;
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
import com.huawei.hms.dci.entity.ParamsInfoEntity;
import com.huawei.hms.dci.entity.QueryRevokeDciInfoEntity;
import com.huawei.hms.dci.entity.WorkDciInfoEntity;
import com.huawei.hms.dci.function.HwDciException;
import com.huawei.hms.dci.function.HwDciPublicClient;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

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

    private Uri selectUri;

    private ImageView mImageView;

    private boolean isRevokingWork;

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
    }

    private void initView() {
        mImageView = findViewById(R.id.iv_dci_logo);
        findViewById(R.id.btn_select_picture).setOnClickListener(this);
        findViewById(R.id.btn_dci_registration).setOnClickListener(this);
        findViewById(R.id.btn_query_result).setOnClickListener(this);
        findViewById(R.id.btn_add_dci_watermark).setOnClickListener(this);
        findViewById(R.id.btn_revoke_copyright).setOnClickListener(this);
        findViewById(R.id.btn_query_revoke_dci_info).setOnClickListener(this);
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
            try {
                // state DCI Registered Work Status：1:success 2:fail 3:revoked 4:manual review
                String workId = data.getQueryParameter("workId");
                String state = data.getQueryParameter("state");
                String dciCode = data.getQueryParameter("dciCode");
                Log.e(TAG, "DCI Registered Work ID = " + workId);
                Log.e(TAG, "The status of the work registered by DCI = " + state);
                Log.e(TAG, "DCI code = " + dciCode);
            } catch (RuntimeException ex) {
                Log.e(TAG, "uri getQueryParameter exception");
            }
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
            case R.id.btn_query_revoke_dci_info:
                // query revoke dci info
                queryRevokeDciInfo();
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
            if (data.getData() == null) {
                return;
            }
            selectUri = data.getData();
            try {
                ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(selectUri, "r");
                if (fd == null) {
                    return;
                }
                showLoading();
                getBitmap(fd);
            } catch (FileNotFoundException e) {
                dismissDialog();
                Log.e(TAG, "openFileDescriptor FileNotFoundException");
            }
        }
    }

    private void getBitmap(ParcelFileDescriptor fd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileDescriptor fileDescriptor = fd.getFileDescriptor();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateInSampleSize(options, 800, 800);
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                Message message = Message.obtain();
                message.obj = bitmap;
                mDealImageHandler.sendMessage(message);
                try {
                    fd.close();
                } catch (IOException e) {
                    Log.e(TAG, "ParcelFileDescriptor close IOException");
                }
            }
        }).start();
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICTURE_REQUEST_CODE);
    }

    private void applyDciCode() {
        if (selectUri == null) {
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
                    selectUri,
                    "北京市",
                    System.currentTimeMillis(),
                    new CommonFailCallBack<String>() {
                        @Override
                        public void onSuccess(String workId) {
                            Log.e(TAG, "DCI Registered Work ID = " + workId);
                            isRevokingWork = false;
                            mDciCode = null;
                            mWorkId = workId;
                            dismissDialog();
                            Toast.makeText(RegistrationActivity.this, getString(R.string.uploading_registered_work_succeeded), Toast.LENGTH_SHORT).show();
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
            HwDciPublicClient.queryWorkDciInfo(paramsInfoEntity, new CommonFailCallBack<WorkDciInfoEntity>() {
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
            HwDciPublicClient.revokeDciCode(paramsInfoEntity, mDciCode, new CommonFailCallBack<String>() {
                @Override
                public void onSuccess(String workId) {
                    dismissDialog();
                    isRevokingWork = true;
                    mWorkId = workId;
                    Toast.makeText(RegistrationActivity.this, getString(R.string.apply_revoke_dci_success), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (HwDciException e) {
            dismissDialog();
            Log.e(TAG, e.getMessage());
        }
    }

    private void queryRevokeDciInfo() {
        if (!isRevokingWork) {
            Toast.makeText(this, getString(R.string.please_revoke_dci_info_first), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        ParamsInfoEntity paramsInfoEntity = DataUtils.getCommonParamsInfoEntity();
        paramsInfoEntity.setDciUid(DataUtils.getDciUid());
        paramsInfoEntity.setWorkId(mWorkId);
        try {
            HwDciPublicClient.queryRevokeDciCodeInfo(paramsInfoEntity, new CommonFailCallBack<QueryRevokeDciInfoEntity>() {
                @Override
                public void onSuccess(QueryRevokeDciInfoEntity result) {
                    dismissDialog();
                    if (result == null) {
                        return;
                    }
                    // 0:dealing 1:success 2:fail
                    if (result.getCode() == 1) {
                        mDciCode = null;
                        isRevokingWork = false;
                        mWorkId = null;
                        Toast.makeText(RegistrationActivity.this, getString(R.string.revoke_dci_registration_success), Toast.LENGTH_SHORT).show();
                    } else if (result.getCode() == 0) {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.revoke_dci_registration_processing), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.revoke_dci_registration_failed) + ":" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (HwDciException ex) {
            dismissDialog();
            Log.e(TAG, ex.getMessage());
        }
    }

    private void addDciWatermark() {
        if (selectUri == null) {
            Toast.makeText(this, getString(R.string.please_select_picture_first), Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        try {
            HwDciPublicClient.addDciWatermark(selectUri, new CommonFailCallBack<String>() {
                @Override
                public void onSuccess(String strBitmap) {
                    dismissDialog();
                    if (!TextUtils.isEmpty(strBitmap)) {
                        stringToBitmap(strBitmap);
                    }
                }
            });
        } catch (HwDciException ex) {
            dismissDialog();
            Log.e(TAG, ex.getMessage());
        }
    }

    private void stringToBitmap(String string) {
        new Thread(() -> {
            try {
                byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
                if (bitmapArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    mDealImageHandler.sendMessage(message);
                }
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "decode base64 exception");
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
