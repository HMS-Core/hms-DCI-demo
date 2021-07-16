# 华为DCI版权服务安卓示例代码

中文 | [English](README.md)

## 目录

* [简介](#简介)
* [开发准备](#开发准备)
* [环境要求](#环境要求)
* [示例代码](#示例代码)
* [运行结果](#运行结果)
* [授权许可](#授权许可)

简介
------------

DCI版权服务（Digital Copyright Identifier Kit，以下简称“DCI Kit”）是由华为和中国版权保护中心（即数字版权唯一标识符管理机构）合作，按照《中华人民共和国著作权法》、“数字版权唯一标识符”标准及相关规定，利用区块链和大数据、人工智能等技术，对数字作品版权进行保护，提供DCI版权服务用户注册、DCI登记服务、DCI维权服务等能力。DCI登记成功的作品信息（包括DCI码）将保存在区块链中，保证所有的版权信息可信、可回溯。
## 开发准备

我们提供一个示例展示如何使用DCI Kit安卓SDK。该示例使用Gradle编译系统。

1. 检查Android Studio开发环境。在Android Studio中打开示例项目，其中含有 "build.gradle" 文件。

2. 注册[华为帐号](https://developer.huawei.com/consumer/cn/)，成为华为开发者。创建应用并在AppGallery Connect中配置相关信息，具体请参考[开发准备](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050196065)，集成 [HUAWEI Account Kit](https://developer.huawei.com/consumer/cn/hms/huawei-accountkit)，如果需要DCI登记发码有通知能力，集成 [Push Kit](https://developer.huawei.com/consumer/cn/hms/huawei-pushkit)。

3. 配置示例代码：

   （1）从AppGallery Connect下载您应用的agconnect-services.json文件，并添加到示例代码的app目录下（\app），替换app下build.gradle的APP_ID为您申请的。

   （2）将示例工程的应用级build.gradle文件中“applicationId”的值更改为您自己的应用包名。

   （3）需要生成签名证书指纹并将证书文件添加到项目中，并在app下的build.gradle文件中配置。

## 环境要求

- Android Studio推荐3.X以上，Android SDK版本号为24或以上，JDK版本号为1.8或以上。
- 支持的场景：华为手机、华为平板、非华为的安卓系统手机。
- 支持的OS：EMUI 8.0+、Android 7.0+。
- HMS Core（APK）版本：5.0.1.301及以上。

## 示例代码

该示例代码为您提供了DCI版权服务用户注册、获取DCI版权服务用户信息、注销DCI版权服务用户、摄影作品DCI登记、查询摄影作品DCI登记信息、撤销摄影作品的DCI登记、对DCI登记成功的摄影作品添加DCI标。

1. MainActivity.java 提供DCI版权服务用户注册、获取DCI版权服务用户信息、注销DCI版权服务用户能力。代码位于app\src\main\java\com\huawei\codelab\dcidemo\MainActivity.java 中。
2. RegistrationActivity.java  提供摄影作品DCI登记、查询摄影作品DCI登记信息、撤销摄影作品的DCI登记、对DCI登记成功的摄影作品添加DCI标能力。代码位于app\src\main\java\com\huawei\codelab\dcidemo\RegistrationActivity.java 中。

## 运行结果

该示例代码在日志中打印DCI版权服务用户信息，DCI摄影作品登记信息的内容，Toast弹窗提示注销DCI版权服务用户，撤销摄影作品的DCI登记的结果。

## 技术支持

如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：

- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在`huawei-mobile-services`标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

## 授权许可

该示例代码经过[Apache 2.0授权许可](http://www.apache.org/licenses/LICENSE-2.0)。