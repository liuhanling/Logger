package com.liuhanling.logger;

public interface Printer {

    Printer t(String tag);

    void v(Object object);

    void v(String message, Throwable tr);

    void v(String message, Object... args);

    void d(Object object);

    void d(String message, Throwable tr);

    void d(String message, Object... args);

    void i(Object object);

    void i(String message, Throwable tr);

    void i(String message, Object... args);

    void w(Object object);

    void w(String message, Throwable tr);

    void w(String message, Object... args);

    void e(Object object);

    void e(String message, Throwable tr);

    void e(String message, Object... args);

    void a(Object object);

    void a(String message, Throwable tr);

    void a(String message, Object... args);

    void c(String message, Throwable tr);

    void j(String json);

    void j(String message, String json);

    void x(String xml);

    void x(String message, String xml);

    void log(int priority, String message, Throwable tr);

    void log(int priority, String message, Object... args);
}
