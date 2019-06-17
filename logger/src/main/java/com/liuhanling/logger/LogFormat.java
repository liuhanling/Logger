package com.liuhanling.logger;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class LogFormat {

    static final String FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "logger";

    static final int LOG_SIZE = 4000;
    private static final int MIN_STACK_OFFSET = 5;

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";

    static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER;
    static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER;
    static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER;
    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * 栈位置
     *
     * @param trace
     * @return
     */
    static int getStackOffset(StackTraceElement[] trace) {
        for (int i = LogFormat.MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    /**
     * 获取栈信息
     *
     * @param stack
     * @return
     */
    static String getStackInfo(StackTraceElement stack, String level) {
        StringBuilder builder = new StringBuilder();
        builder.append(LogFormat.HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(stack.getClassName())
                .append(".")
                .append(stack.getMethodName())
                .append(" (")
                .append(stack.getFileName())
                .append(":")
                .append(stack.getLineNumber())
                .append(")");
        return builder.toString();
    }

    /**
     * 获取tag
     *
     * @param temp
     * @param tag
     * @return
     */
    static String getFormatTag(String temp, String tag) {
        if (!Utils.isEmpty(temp) && !Utils.equals(temp, tag)) {
            return tag + "-" + temp;
        }
        return tag;
    }

    /**
     * 获取标准打印换行
     *
     * @param priority
     * @param tag
     * @return
     */
    static String getFormatLog(int priority, String tag, String msg) {
        String time = TIMESTAMP.format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append(time)
                .append(' ')
                .append(tag)
                .append('(')
                .append(Utils.getLevel(priority))
                .append(')')
                .append(':')
                .append(msg)
                .append("\r\n");
        return builder.toString();
    }
}
