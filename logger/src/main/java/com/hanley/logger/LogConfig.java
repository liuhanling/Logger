package com.hanley.logger;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

public class LogConfig implements Config {

    private final Context context;
    private final int showMethod;
    private final boolean showThread;
    private final boolean printLog;
    private final boolean writeLog;
    private final boolean crashLog;
    private final CrashCall callback;
    private final String tag;

    private LoggerWriter loggerWriter;

    private LogConfig(Builder builder) {
        Utils.checkNotNull(builder);
        this.context = builder.context;
        this.showThread = builder.showThread;
        this.showMethod = builder.showMethod;
        this.printLog = builder.printLog;
        this.writeLog = builder.writeLog;
        this.crashLog = builder.crashLog;
        this.callback = builder.callback;
        this.tag = builder.tag;
        this.initWriter();
    }

    private void initWriter() {
        if (writeLog || crashLog) {
            HandlerThread thread = new HandlerThread("AndroidFileLogger." + LogFormat.FOLDER);
            thread.start();
            loggerWriter = new LoggerWriter(thread.getLooper(), context, LogFormat.FOLDER, callback);
        }
    }

    @Override
    public void log(int priority, String tag, String message) {
        if (printLog || writeLog || crashLog) {
            tag = LogFormat.getFormatTag(tag, this.tag);
            logTopBorder(priority, tag);
            logThread(priority, tag);
            logMethod(priority, tag);
            logMessage(priority, tag, message);
            logBottomBorder(priority, tag);
        }
    }

    /**
     * 顶部信息
     *
     * @param priority
     * @param tag
     */
    private void logTopBorder(int priority, String tag) {
        logPrint(priority, tag, LogFormat.TOP_BORDER);
    }

    /**
     * 线程信息
     *
     * @param priority
     * @param tag
     */
    private void logThread(int priority, String tag) {
        if (showThread) {
            logPrint(priority, tag, LogFormat.HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(priority, tag);
        }
    }

    /**
     * 打印方法
     *
     * @param priority
     * @param tag
     */
    private void logMethod(int priority, String tag) {
        boolean isCrash = priority == ApplicationErrorReport.TYPE_CRASH;
        if (showMethod > 0 || isCrash) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (Utils.isEmpty(trace)) return;

            int method = isCrash ? 5 : this.showMethod;
            int offset = LogFormat.getStackOffset(trace);
            if (method + offset > trace.length) {
                method = trace.length - offset - 1;
            }

            String level = "";
            for (int i = method; i > 0; i--) {
                int index = i + offset;
                if (index >= trace.length) {
                    continue;
                }
                logPrint(priority, tag, LogFormat.getStackInfo(trace[index], level));
                level += "   ";
            }
            logDivider(priority, tag);
        }
    }

    /**
     * 底部边线
     *
     * @param priority
     * @param tag
     */
    private void logBottomBorder(int priority, String tag) {
        logPrint(priority, tag, LogFormat.BOTTOM_BORDER);
    }

    /**
     * 内分隔线
     *
     * @param priority
     * @param tag
     */
    private void logDivider(int priority, String tag) {
        logPrint(priority, tag, LogFormat.MIDDLE_BORDER);
    }

    /**
     * 系统分行
     *
     * @param message
     * @param tag
     */
    private void logMessage(int priority, String tag, String message) {
        String[] array = message.split(LogFormat.LINE_SEPARATOR);
        for (String content : array) {
            logContent(priority, tag, content);
        }
    }

    /**
     * 长度分行
     *
     * @param message
     * @param tag
     */
    private void logContent(int priority, String tag, String message) {
        byte[] bytes = message.getBytes();
        int len = bytes.length;
        if (len <= LogFormat.CHUNK_SIZE) {
            logContentLine(priority, tag, message);
            return;
        }
        for (int i = 0; i < len; i += LogFormat.CHUNK_SIZE) {
            int count = Math.min(len - i, LogFormat.CHUNK_SIZE);
            logContentLine(priority, tag, new String(bytes, i, count));
        }
    }

    /**
     * 打印一行
     *
     * @param message
     * @param tag
     */
    private void logContentLine(int priority, String tag, String message) {
        logPrint(priority, tag, LogFormat.HORIZONTAL_LINE + " " + message);
    }

    /**
     * 系统打印
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void logPrint(int priority, String tag, String message) {
        Utils.checkNotNull(message);
        if (printLog) {
            Log.println(priority, tag, message);
        }
        if (writeLog && priority>= Log.VERBOSE && priority <= Log.ASSERT) {
            String msg = LogFormat.getFormatLog(priority, tag, message);
            loggerWriter.sendMessage(loggerWriter.obtainMessage(priority, msg));
            return;
        }
        if (crashLog && priority == ApplicationErrorReport.TYPE_CRASH) {
            String msg = LogFormat.getFormatLog(priority, tag, message);
            loggerWriter.sendMessage(loggerWriter.obtainMessage(priority, msg));
        }
    }

    public static class Builder {

        private Context context;
        private int showMethod = 1;
        private boolean showThread = false;
        private boolean printLog = true;
        private boolean writeLog = false;
        private boolean crashLog = false;
        private CrashCall callback;
        private String tag = Logger.TAG;

        public Builder(Context context) {
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

        public Builder crashLog(boolean write, CrashCall callback) {
            this.crashLog = write;
            this.callback = callback;
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