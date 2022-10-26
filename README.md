[![](https://jitpack.io/v/liuhanling/Logger.svg)](https://jitpack.io/#liuhanling/Logger)

### Android Logger
简单强大的Android日志管理器

### Feature

- 支持格式化打印Log
- 支持基本数据类型、字符串、对象、数组、集合、Json、Xml、Throwable等打印
- 支持本地保存log，路径：/logger/yyyy-MM-dd/yyyy_MM_dd_HHmmss.log
- 支持本地保存crash，路径：/logger/yyyy-MM-dd/crash_yyyy_MM_dd_HHmmss.log

### Dependency

1. Add it in your root `build.gradle` at the end of repositories

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

2. Add the dependency to your moudle `build.gradle`

```gradle
dependencies {
    implementation 'com.github.liuhanling:Logger:1.5'
}
```

### Usage

AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

注意：Build.VERSION.SDK_INT >=23, 请动态申请权限.
```

Application

```java
// 简洁配置
Logger.init(this);
```
 Or

```java
// 自定义配置
LogConfig config = LogConfig.builder(this)
		.formatLog(true)            // (可选) 打印信息格式，默认false
		.showThread(true)           // (可选) 打印线程信息，默认false
		.showMethod(1)              // (可选) 打印方法行数，默认0
		.printLog(true)             // (可选) 是否打印日志，默认true
		.writeLog(true)             // (可选) 是否保存日志，默认true
		.crashLog(true)             // (可选) 是否保存异常，默认true
		.crashCall(e -> {           // (可选) 全局异常处理，默认kill
			// AppManager.getInstance().exit();
		})
		.path("/logger")            // (可选) 配置存储目录，默认/logger
		.tag("LOVE_LOGGER")         // (可选) 配置日志标记，默认LOGGER
		.build();
Logger.init(config);
```

### Output
<img src='https://github.com/liuhanling/Logger/blob/master/preview/logcat.png'/>

### Options

```java
Logger.v(Object object);
Logger.v(String message, Object object);

Logger.d(Object object);
Logger.d(String message, Object object);

Logger.i(Object object);
Logger.i(String message, Object object);

Logger.w(Object object);
Logger.w(String message, Object object);

Logger.e(Object object);
Logger.e(String message, Object object);

Logger.a(Object object);
Logger.a(String message, Object object);

Logger.c(Object object);
Logger.c(String message, Object object);

Logger.j(String json);
Logger.j(String message, String json);

Logger.x(String xml);
Logger.x(String message, String xml);

Logger.log(int priority, Object object);
Logger.log(int priority, String message, Object object);
```

Support object
```java
Logger.v(object);
Logger.d(object);
Logger.i(object);
Logger.w(object);
Logger.e(object);
Logger.a(object);
```

Support collections
```java
Logger.v(MAP/SET/LIST/ARRAY);
Logger.d(MAP/SET/LIST/ARRAY);
Logger.i(MAP/SET/LIST/ARRAY);
Logger.w(MAP/SET/LIST/ARRAY);
Logger.e(MAP/SET/LIST/ARRAY);
Logger.a(MAP/SET/LIST/ARRAY);
```

Support throwable
```java
Logger.e(e);
Logger.e("Error:", e);
```

Support crash

```java
// 单独写入crash.log
Logger.c(tr);
Logger.c("Crash:", tr);
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

Support tag

```java
Logger.tag("tag").v(object);
```

###  License

<pre>
MIT License

Copyright (c) 2018 liuhanling

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
</pre>
