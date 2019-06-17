package com.liuhanling.logger;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

public class LogConfig implements Config {

    private final Context context;
    private final int method;
    private final boolean showThread;
    private final boolean formatLog;
    private final boolean printLog;
    private final boolean writeLog;
    private final boolean crashLog;
    private final CrashCall callback;
    private final String tag;

    private LogWriter logWriter;

    private LogConfig(Builder builder) {
        Utils.checkNotNull(builder);
        this.context = builder.context;
        this.showThread = builder.showThread;
        this.method = builder.showMethod;
        this.formatLog = builder.formatLog;
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
            logWriter = new LogWriter(thread.getLooper(), LogFormat.FOLDER);
        }
        if (crashLog) {
            new CrashLog(context, callback).start();
        }
    }

    @Override
    public void log(int priority, String tag, String message) {
        if (printLog || writeLog || crashLog) {
            tag = LogFormat.getFormatTag(tag, this.tag);
            if (formatLog) {
                printTopper(priority, tag);
                printThread(priority, tag);
                printMethod(priority, tag);
                printMessage(priority, tag, message);
                printBottom(priority, tag);
            } else {
                writeMessage(priority, tag, message);
            }
        }
    }

    /**
     * 打印顶部框
     *
     * @param priority
     * @param tag
     */
    private void printTopper(int priority, String tag) {
        println(priority, tag, LogFormat.TOP_BORDER);
    }

    /**
     * 打印低部框
     *
     * @param priority
     * @param tag
     */
    private void printBottom(int priority, String tag) {
        println(priority, tag, LogFormat.BOTTOM_BORDER);
    }

    /**
     * 打印分隔线
     *
     * @param priority
     * @param tag
     */
    private void printDivider(int priority, String tag) {
        println(priority, tag, LogFormat.MIDDLE_BORDER);
    }

    /**
     * 打印线程
     *
     * @param priority
     * @param tag
     */
    private void printThread(int priority, String tag) {
        if (showThread) {
            println(priority, tag, LogFormat.HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
            printDivider(priority, tag);
        }
    }

    /**
     * 打印方法
     *
     * @param priority
     * @param tag
     */
    private void printMethod(int priority, String tag) {
        if (this.method > 0) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (Utils.isEmpty(trace)) return;

            int count = this.method;
            int offset = LogFormat.getStackOffset(trace);
            if (count + offset > trace.length) {
                count = trace.length - offset - 1;
            }

            String level = "";
            for (int i = count; i > 0; i--) {
                int index = i + offset;
                if (index >= trace.length) {
                    continue;
                }
                println(priority, tag, LogFormat.getStackInfo(trace[index], level));
                level += "  ";
            }
            printDivider(priority, tag);
        }
    }

    /**
     * 打印信息
     *
     * @param message
     * @param tag
     */
    private void printMessage(int priority, String tag, String message) {
        String[] array = message.split(LogFormat.LINE_SEPARATOR);
        for (String msg : array) {
            byte[] bytes = msg.getBytes();
            int len = bytes.length;
            if (len <= LogFormat.LOG_SIZE) {
                println(priority, tag, LogFormat.HORIZONTAL_LINE + " " + msg);
                writeln(priority, tag, msg);
                continue;
            }
            for (int i = 0; i < len; i += LogFormat.LOG_SIZE) {
                int count = Math.min(len - i, LogFormat.LOG_SIZE);
                String str = new String(bytes, i, count);
                println(priority, tag, LogFormat.HORIZONTAL_LINE + " " + str);
                writeln(priority, tag, str);
            }
        }
    }

    /**
     * 打印信息
     *
     * @param message
     * @param tag
     */
    private void writeMessage(int priority, String tag, String message) {
        String[] array = message.split(LogFormat.LINE_SEPARATOR);
        for (String msg : array) {
            byte[] bytes = msg.getBytes();
            int len = bytes.length;
            if (len <= LogFormat.LOG_SIZE) {
                println(priority, tag, msg);
                writeln(priority, tag, msg);
                continue;
            }
            for (int i = 0; i < len; i += LogFormat.LOG_SIZE) {
                int count = Math.min(len - i, LogFormat.LOG_SIZE);
                String str = new String(bytes, i, count);
                println(priority, tag, str);
                writeln(priority, tag, str);
            }
        }
    }

    /**
     * 系统打印
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void println(int priority, String tag, String message) {
        Utils.checkNotNull(message);
        if (printLog) {
            int level = priority == LogPrinter.CRASH ? Log.ERROR : priority;
            Log.println(level, tag, message);
        }
    }

    /**
     * 写入信息
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void writeln(int priority, String tag, String message) {
        Utils.checkNotNull(message);
        if ((writeLog && priority <= Log.ASSERT) || (crashLog && priority == LogPrinter.CRASH)) {
            message = LogFormat.getFormatLog(priority, tag, message);
            logWriter.sendMessage(logWriter.obtainMessage(priority, message));
        }
    }

    public static class Builder {

        private Context context;
        private int showMethod = 1;
        private boolean showThread = false;
        private boolean formatLog = BuildConfig.DEBUG;
        private boolean printLog = BuildConfig.DEBUG;
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