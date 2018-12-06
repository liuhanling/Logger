package com.hanley.logger;

public class LogPrint implements Printer {

    @Override
    public Printer t(String tag) {
        return null;
    }

    @Override
    public void v(String message, Object... args) {

    }

    @Override
    public void d(String message, Object... args) {

    }

    @Override
    public void d(Object object) {

    }

    @Override
    public void w(String message, Object... args) {

    }

    @Override
    public void i(String message, Object... args) {

    }

    @Override
    public void e(String message, Object... args) {

    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {

    }

    @Override
    public void a(String message, Object... args) {

    }

    @Override
    public void json(String json) {

    }

    @Override
    public void xml(String xml) {

    }

    @Override
    public void log(int priority, String tag, String message, Throwable throwable) {

    }
}
