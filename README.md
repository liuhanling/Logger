[![](https://jitpack.io/v/liuhanling/Logger.svg)](https://jitpack.io/#liuhanling/Logger)

# Android Logger
Android 日志框架。

Feature
--------------
- 支持格式化Log
- 支持基本数据类型、数组、集合、json、xml等打印
- 支持本地保存Log
- 支持本地保存Crash Log

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
    implementation 'com.github.liuhanling:Logger:1.2'
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
        .showThread(true)               // (可选) 显示线程信息，默认false
        .showMethod(5)                  // (可选) 显示方法条数，默认1
        .printLog(BuildConfig.DEBUG)    // (可选) 是否打印日志，默认true
        .writeLog(true)                 // (可选) 是否保存日志，默认false
        .crashLog(true)                 // (可选) 是否保存异常，默认false
        .tag("LOVE_LOGGER")             // (可选) 定义日志标记，默认LOVE_LOGGER
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

Support string format arguments
```java
Logger.d("hello %s", "world");
```

Support collections
```java
Logger.d(MAP);
Logger.d(SET);
Logger.d(LIST);
Logger.d(ARRAY);
```

Support Xml and Json
```java
Logger.xml(XML);
Logger.json(JSON);
```
