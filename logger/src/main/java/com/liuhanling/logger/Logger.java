package com.liuhanling.logger;

import android.content.Context;

/**
 * 日志管理类
 */
public final class Logger {

    private Printer printer = null;

    private Logger() {
    }

    private static final class Holder {
        private static final Logger INSTANCE = new Logger();
    }

    private static Logger getInstance() {
        return Holder.INSTANCE;
    }

    public static void init(Context context) {
        if (getInstance().printer == null) {
            getInstance().printer = new LogPrinter(context);
        }
    }

    public static void init(LogConfig config) {
        if (getInstance().printer == null) {
            getInstance().printer = new LogPrinter(config);
        }
    }

    public static Printer tag(String tag) {
        return getInstance().printer.tag(tag);
    }

    public static void v(Object object) {
        getInstance().printer.w(object);
    }

    public static void v(String message, Object object) {
        getInstance().printer.v(message, object);
    }

    public static void d(Object object) {
        getInstance().printer.d(object);
    }

    public static void d(String message, Object object) {
        getInstance().printer.d(message, object);
    }

    public static void i(Object object) {
        getInstance().printer.i(object);
    }

    public static void i(String message, Object object) {
        getInstance().printer.i(message, object);
    }

    public static void w(Object object) {
        getInstance().printer.w(object);
    }

    public static void w(String message, Object object) {
        getInstance().printer.w(message, object);
    }

    public static void e(Object object) {
        getInstance().printer.e(object);
    }

    public static void e(String message, Object object) {
        getInstance().printer.e(message, object);
    }

    public static void a(Object object) {
        getInstance().printer.a(object);
    }

    public static void a(String message, Object object) {
        getInstance().printer.a(message, object);
    }

    public static void c(Object object) {
        getInstance().printer.c(object);
    }

    public static void c(String message, Object object) {
        getInstance().printer.c(message, object);
    }

    public static void j(String json) {
        getInstance().printer.j(json);
    }

    public static void j(String message, String json) {
        getInstance().printer.j(message, json);
    }

    public static void x(String xml) {
        getInstance().printer.x(xml);
    }

    public static void x(String message, String xml) {
        getInstance().printer.x(message, xml);
    }

    public static void log(int priority, String message, Object object) {
        getInstance().printer.log(priority, message, object);
    }

}
