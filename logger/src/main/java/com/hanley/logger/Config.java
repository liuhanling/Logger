package com.hanley.logger;

public interface Config {
    void log(int priority, String tag, String message);
}