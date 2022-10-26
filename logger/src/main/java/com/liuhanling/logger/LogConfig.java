package com.liuhanling.logger;

import android.content.Context;

public class LogConfig {

    final Context context;
    final int showMethod;
    final boolean showThread;
    final boolean formatLog;
    final boolean printLog;
    final boolean writeLog;
    final boolean crashLog;
    final String path;
    final CrashCall callback;
    final String tag;

    public static LogConfig.Builder builder(Context context) {
        return new LogConfig.Builder(context);
    }

    private LogConfig(Builder builder) {
        this.context = builder.context;
        this.showThread = builder.showThread;
        this.showMethod = builder.showMethod;
        this.formatLog = builder.formatLog;
        this.printLog = builder.printLog;
        this.writeLog = builder.writeLog;
        this.crashLog = builder.crashLog;
        this.callback = builder.callback;
        this.path = builder.path;
        this.tag = builder.tag;
    }

    public static class Builder {

        private Context context;
        private int showMethod = 0;
        private boolean showThread = false;
        private boolean formatLog = false;
        private boolean printLog = true;
        private boolean writeLog = true;
        private boolean crashLog = true;
        private CrashCall callback;
        private String path = Config.LOG_PATH;
        private String tag = Config.LOG_TAG;

        private Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder showThread(boolean show) {
            this.showThread = show;
            return this;
        }

        public Builder showMethod(int count) {
            this.showMethod = count;
            return this;
        }

        public Builder formatLog(boolean format) {
            this.formatLog = format;
            return this;
        }

        public Builder printLog(boolean print) {
            this.printLog = print;
            return this;
        }

        public Builder writeLog(boolean write) {
            this.writeLog = write;
            return this;
        }

        public Builder crashLog(boolean write) {
            this.crashLog = write;
            return this;
        }

        public Builder crashCall(CrashCall callback) {
            this.callback = callback;
            return this;
        }

        public Builder path(String path) {
            this.path = Config.ROOT_PATH + path;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public LogConfig build() {
            return new LogConfig(this);
        }
    }
}