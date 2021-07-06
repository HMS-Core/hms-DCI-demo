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

/**
 *  DCI Kit Error code class.
 *
 * @since 2021-06-02
 */
public class ErrorCode {
    /**
     * DCI copyRight account register failed.
     */
    public static final int NOT_REGISTER = 50000007;

    /**
     * Get DCI Account Information Success.
     */
    public static final int GET_DCI_ACCOUNT_SUCCESS_CODE = 200;

    /**
     * Real-Name Authentication Failure Caused by Forcible Exit.
     */
    public static final int REAL_NAME_FAIL_AS_FORCIBLE_EXIT = 10001001;

    /**
     * Hms token or openId is null.
     */
    public static final int REAL_NAME_FAIL_AS_TOKEN_OR_OPENID_NULL = 10001002;

    /**
     * Hms token or openId Unavailable.
     */
    public static final int HW_TOKEN_OR_UID_NOT_USED = 10001003;
}
