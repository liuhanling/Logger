[![](https://jitpack.io/v/liuhanling/Logger.svg)](https://jitpack.io/#liuhanling/Logger)

# Android Logger
Android 日志框架。

Feature
--------------
- 支持格式化打印Log
- 支持基本数据类型、字符串、数组、集合、Json、Xml、Throwable等打印
- 支持本地保存Log
- 支持本地保存Crash Log，以及Crash回调处理

Dependency
--------------

1. Add it in your root `build.gradle` at the end of repositories

```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2. Add the dependency to your moudle `build.gradle`

```gradle
dependencies {
    implementation 'com.github.liuhanling:Logger:1.3'
}
```

Usage
--------------

AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

Application

```java
Logger.init(this);
```
 Or
 
```java
LogConfig config = new LogConfig.Builder(this)
        .showThread(true)                   // (可选) 显示线程信息，默认false
        .showMethod(0)                      // (可选) 显示方法条数，默认1
        .printLog(BuildConfig.DEBUG)        // (可选) 是否打印日志，默认BuildConfig.DEBUG
        .writeLog(true)                     // (可选) 是否保存日志，默认false
        .crashLog(true)                     // (可选) 是否保存异常，默认false
//      .crashLog(true, new CrashCall() {   // (可选) 设置异常回调，默认无
//          @Override
//          public void handle() {
//              // remove activities and exit
//          }
//      })
        .tag("LOVE_LOGGER")                 // (可选) 定义日志标记，默认LOVE_LOGGER
        .build();

Logger.init(config);
```

Options
--------------

```java
Logger.v("verbose");
Logger.d("debug");
Logger.i("info");
Logger.w("warn");
Logger.e("error");
Logger.a("assert");
```

Support string format
```java
Logger.d("Hello %s", "Logger");
```

Support throwable
```java
Logger.e(e);
Logger.e("Error:", e);
```

Support collections
```java
Logger.d(MAP/SET/LIST/ARRAY);
```

Support Xml
```java
Logger.x(XML);
Logger.x("xml:", XML);
```

Support Json
```java
Logger.j(JSON);
Logger.j("json:", JSON);
```
