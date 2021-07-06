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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.huawei.codelab.dcidemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission Utils class.
 *
 * @since 2021-06-02
 */
public final class PermissionsUtils {
    private static String[] PERMISSIONS =
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static volatile PermissionsUtils permissionsUtils;

    private static final int REQUEST_CODE = 100;

    private IPermissionsResult mPermissionsResult;

    private List<String> forbidPermissionLists;

    private PermissionsUtils() {
    }

    /**
     * PermissionsUtils Instance.
     *
     * @return Instance
     */
    public static PermissionsUtils getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new PermissionsUtils();
        }
        return permissionsUtils;
    }

    /**
     * Check permissions.
     *
     * @param activity          Activity
     * @param permissionArray   need check permissions
     * @param permissionsResult IPermissionsResult
     */
    public void checkPermissions(
            Activity activity, String[] permissionArray, @NonNull IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;
        List<String> permissionLists = new ArrayList<>();
        forbidPermissionLists = new ArrayList<>();
        for (String permission : permissionArray) {
            if (PermissionChecker.checkSelfPermission(activity, permission) != PermissionChecker.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }
        if (!permissionLists.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionLists.toArray(new String[0]), REQUEST_CODE);
        } else {
            permissionsResult.passPermissions();
        }
    }

    /**
     * Callback method after permission request.
     *
     * @param activity        Activity
     * @param requestCode     request code
     * @param permissionArray permissionArray
     * @param grantResults   grant results
     */
    public void onRequestPermissionsResult(
            Activity activity, int requestCode, @NonNull String[] permissionArray, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (grantResults.length > 0) {
            permissionResult(activity, permissionArray, grantResults);
        }
    }

    private void permissionResult(Activity activity, @NonNull String[] permissionArray, @NonNull int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            int grantResult = grantResults[i];
            String permission = permissionArray[i];
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    if (forbidPermissionLists != null) {
                        forbidPermissionLists.add(permission);
                    }
                } else {
                    deniedPermissions.add(permission);
                }
            }
        }
        if (mPermissionsResult == null) {
            return;
        }
        if (deniedPermissions.isEmpty() && forbidPermissionLists != null && forbidPermissionLists.isEmpty()) {
            mPermissionsResult.passPermissions();
        } else if (!deniedPermissions.isEmpty()) {
            mPermissionsResult.forbidPermissions(deniedPermissions);
        } else {
            Toast.makeText(
                    activity, activity.getString(R.string.please_go_to_setting),
                    Toast.LENGTH_LONG)
                    .show();
            if (forbidPermissionLists != null) {
                forbidPermissionLists.clear();
            }
        }
    }

    /**
     * Obtaining Required Permissions.
     *
     * @return String array
     */
    public static String[] getPermissions() {
        return PERMISSIONS.clone();
    }

    /**
     * Permission Result Callback Interface.
     */
    public interface IPermissionsResult {
        /**
         * Pass permissions.
         */
        void passPermissions();

        /**
         * Authorization failed.
         *
         * @param deniedPermissions denied permissions
         */
        void forbidPermissions(List<String> deniedPermissions);
    }
}
