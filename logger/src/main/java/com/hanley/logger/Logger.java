package com.hanley.logger;

import android.content.Context;

public final class Logger {

    private static Printer printer = null;

    private Logger() {
    }

    public static void init(Context context) {
        if (printer == null) {
            printer = new LoggerPrinter(context);
        }
    }

    public static void init(Config config) {
        if (printer == null) {
            printer = new LoggerPrinter(config);
        }
    }

    public static Printer t(String tag) {
        return printer.t(tag);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void d(Object object) {
        printer.d(object);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void e(String message, Object... args) {
        printer.e(message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void a(String message, Object... args) {
        printer.a(message, args);
    }

    public static void log(int priority, String tag, String message, Throwable throwable) {
        printer.log(priority, tag, message, throwable);
    }

    public static void json(String json) {
        printer.json(json);
    }

    public static void xml(String xml) {
        printer.xml(xml);
    }

}
