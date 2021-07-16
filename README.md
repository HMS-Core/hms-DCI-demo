# HUAWEI DCI Kit Sample Code for Android

English | [中文](README_ZH.md)

## Contents

* [Introduction](#introduction)
* [Development Preparations](#development-preparations)
* [Environment Requirements](#environment-requirements)
* [Sample Code](#sample-code)
* [Running Result](#running-result)
* [License](#license)

Introduction
------------

Digital Copyright Identifier Kit (DCI Kit), developed by Huawei and Copyright Protection Center of China (CPCC), protects copyright on digital works by leveraging technologies related to the blockchain, big data, and AI, in compliance with the Copyright Law of People's Republic of China and DCI specifications and regulations. Capabilities provided by this kit include DCI user registration, copyright registration, and copyright safeguarding. Information about successfully registered works (including their DCI codes) will be stored in the blockchain, ensuring that all copyright information is reliable and traceable. 
## Development Preparations

The sample code, which is complied using Gradle, shows how to integrate the DCI SDK for Android into an app.  

1. Install Android Studio on your computer. Open the demo project in Android Studio. You can find build.gradle files in the project.

2. Register as a Huawei developer on [HUAWEI Developers](https://developer.huawei.com/consumer/en/). Create an app and configure app information in AppGallery Connect. For details, please refer to [Preparations](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050196065). Integrate [HUAWEI Account Kit](https://developer.huawei.com/consumer/en/hms/huawei-accountkit). If you want copyright owners to be notified when a DCI code is issued, also integrate [Push Kit](https://developer.huawei.com/consumer/en/hms/huawei-pushkit). 

3. Configure the sample code.

   (1) Download the agconnect-services.json file from AppGallery Connect and add it to the app directory of the demo project.

   (2) Open the app-level build.gradle file. Replace APP_ID with the app ID obtained from AppGallery Connect and applicationId with your app package name.

   (3) Generate a signing certificate fingerprint. Copy the generated signing certificate to the project and configure the signing certificate in the app-level build.gradle file.

## Environment Requirements

- It is recommended that the Android Studio version be 3.0 or later, the Android SDK version be 24 or later, and the JDK version be 1.8 or later.
- Supported devices: Huawei phones and tablets, and non-Huawei Android phones
- Supported operating systems: EMUI 8.0 or later and Android 7.0 or later
- HMS Core (APK) version: 5.0.1.301 or later

## Sample Code

The sample code shows how to use capabilities of DCI Kit, including registration and deregistration of DCI users, obtaining information about a DCI user, registration and deregistration of photographic work copyrights, obtaining work copyright registration information, and adding a DCI icon to registered photographic works.

1. MainActivity.java, which is under the app\src\main\java\com\huawei\codelab\dcidemo directory, is used for registration and deregistration of DCI users and obtaining information about a DCI user.  
2. RegistrationActivity.java, which is under the app\src\main\java\com\huawei\codelab\dcidemo directory, is used for registration and deregistration of photographic work copyrights, obtaining information about photographic work copyright registration, and adding a DCI icon to registered photographic works.  

## Running Result

Information about the registered DCI users and works is printed in the log. In addition, the user deregistration or work copyright deregistration result will be displayed in a toast window. 

## Technical Support

You can visit the [Reddit community](https://www.reddit.com/r/HuaweiDevelopers/) to obtain the latest information about HMS Core and communicate with other developers. 

If you have any questions about the sample code, try the following:

- Visit [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services) and submit your questions under the `huawei-mobile-services` tag. Huawei experts will help you.
- Visit the HMS Core section in the [Huawei Developer Forum](https://forums.developer.huawei.com/forumPortal/en/forum/hms-core) and communicate with other developers.

## License

The sample code is licensed under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0).
