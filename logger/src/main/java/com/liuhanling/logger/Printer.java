package com.liuhanling.logger;

public interface Printer {

    Printer tag(String tag);

    void v(Object object);

    void v(String message, Object object);

    void d(Object object);

    void d(String message, Object object);

    void i(Object object);

    void i(String message, Object object);

    void w(Object object);

    void w(String message, Object object);

    void e(Object object);

    void e(String message, Object object);

    void a(Object object);

    void a(String message, Object object);

    void c(Object object);
    
    void c(String message, Object object);

    void j(String json);

    void j(String message, String json);

    void x(String xml);

    void x(String message, String xml);

    void log(int priority, Object object);

    void log(int priority, String message, Object object);
}
