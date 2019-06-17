package com.liuhanling.logger;

import android.content.Context;

public final class Logger {

    static final String TAG = "LOVE_LOGGER";

    private static Printer printer = null;

    private Logger() {
    }

    public static void init(Context context) {
        if (printer == null) {
            printer = new LogPrinter(context);
        }
    }

    public static void init(Config config) {
        if (printer == null) {
            printer = new LogPrinter(config);
        }
    }

    public static Printer t(String tag) {
        return printer.t(tag);
    }

    public static void w(Object object) {
        printer.w(object);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void d(Object object) {
        printer.d(object);
    }

    public static void d(String message, Throwable tr) {
        printer.d(message, tr);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void w(String message, Object object) {
        printer.w(message, object);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void e(Object object) {
        printer.e(object);
    }

    public static void e(String message, Throwable tr) {
        printer.e(message, tr);
    }

    public static void e(String message, Object... args) {
        printer.e(message, args);
    }

    public static void a(String message, Object... args) {
        printer.a(message, args);
    }

    public static void c(String message, Throwable tr) {
        printer.c(message, tr);
    }

    public static void j(String json) {
        printer.j(json);
    }

    public static void j(String message, String json) {
        printer.j(message, json);
    }

    public static void x(String xml) {
        printer.x(xml);
    }

    public static void x(String message, String xml) {
        printer.x(message, xml);
    }

    public static void log(int priority, String tag, String message, Throwable throwable) {
        printer.log(priority, tag, message, throwable);
    }

}
