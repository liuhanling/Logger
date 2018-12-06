package com.hanley.logger;

import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class LogConfig implements Config {

    private static final int CHUNK_SIZE = 4000;

    private static final int MIN_STACK_OFFSET = 5;

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "logger";

    private final int method;
    private final boolean thread;
    private final boolean print;
    private final boolean write;
    private final String tag;
    private LoggerWriter writer;

    private LogConfig(Builder builder) {
        Utils.checkNotNull(builder);
        this.thread = builder.thread;
        this.method = builder.method;
        this.print = builder.print;
        this.write = builder.write;
        this.tag = builder.tag;
        this.initWriter();
    }

    private void initWriter() {
        if (write) {
            HandlerThread thread = new HandlerThread("AndroidFileLogger." + FOLDER);
            thread.start();
            writer = new LoggerWriter(thread.getLooper(), FOLDER);
        }
    }

    public static Builder Builder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String tag, String message) {
        if (print || write) {
            tag = getFormatTag(tag);

            logTopBorder(priority, tag);
            logHeaderContent(priority, tag, method);

            byte[] bytes = message.getBytes();
            int length = bytes.length;
            if (length <= CHUNK_SIZE) {
                if (method > 0) {
                    logDivider(priority, tag);
                }
                logContent(priority, tag, message);
                logBottomBorder(priority, tag);
                return;
            }
            if (method > 0) {
                logDivider(priority, tag);
            }
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int count = Math.min(length - i, CHUNK_SIZE);
                logContent(priority, tag, new String(bytes, i, count));
            }
            logBottomBorder(priority, tag);
        }
    }

    /**
     * LOG 上边
     *
     * @param priority
     * @param tag
     */
    private void logTopBorder(int priority, String tag) {
        logPrint(priority, tag, TOP_BORDER);
    }

    /**
     * LOG 头部
     *
     * @param priority
     * @param tag
     * @param method
     */
    private void logHeaderContent(int priority, String tag, int method) {
        if (thread) {
            logPrint(priority, tag, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(priority, tag);
        }

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = getStackOffset(trace);
        if (method + stackOffset > trace.length) {
            method = trace.length - stackOffset - 1;
        }

        String level = "";
        for (int i = method; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logPrint(priority, tag, builder.toString());
        }
    }

    /**
     * LOG 底边
     *
     * @param priority
     * @param tag
     */
    private void logBottomBorder(int priority, String tag) {
        logPrint(priority, tag, BOTTOM_BORDER);
    }

    /**
     * LOG 分隔
     *
     * @param priority
     * @param tag
     */
    private void logDivider(int priority, String tag) {
        logPrint(priority, tag, MIDDLE_BORDER);
    }

    /**
     * LOG 内容
     *
     * @param message
     * @param tag
     */
    private void logContent(int priority, String tag, String message) {
        Utils.checkNotNull(message);
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            logPrint(priority, tag, HORIZONTAL_LINE + " " + line);
        }
    }

    /**
     * LOG 打印
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void logPrint(int priority, String tag, String message) {
        Utils.checkNotNull(message);
        if (print) {
            Log.println(priority, tag, message);
        }
        if (write) {
            String time = DateFormat.getDateTimeInstance().format(new Date());
            StringBuilder builder = new StringBuilder();
            builder.append(time)
                    .append(" (")
                    .append(Utils.getLevel(priority))
                    .append(") ")
                    .append(tag)
                    .append(": ")
                    .append(message);
            writer.sendMessage(writer.obtainMessage(priority, builder.toString()));
        }
    }

    /**
     * 栈位置
     *
     * @param trace
     * @return
     */
    private int getStackOffset(StackTraceElement[] trace) {
        Utils.checkNotNull(trace);
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private String getSimpleClassName(String name) {
        Utils.checkNotNull(name);
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private String getFormatTag(String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static class Builder {

        private int method = 2;
        private boolean thread = false;
        private boolean print = true;
        private boolean write = false;
        private String tag = "LOVE_LOGGER";

        private Builder() {
        }

        public Builder thread(boolean show) {
            this.thread = show;
            return this;
        }

        public Builder method(int count) {
            this.method = count;
            return this;
        }

        public Builder print(boolean print) {
            this.print = print;
            return this;
        }

        public Builder write(boolean write) {
            this.write = write;
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
