package com.hanley.logger;

import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class LogFormat {

    public static final String FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar + "logger";

    public static final int CHUNK_SIZE = 4000;
    public static final int MIN_STACK_OFFSET = 5;

    public static final char TOP_LEFT_CORNER = '┌';
    public static final char BOTTOM_LEFT_CORNER = '└';
    public static final char MIDDLE_CORNER = '├';
    public static final char HORIZONTAL_LINE = '│';
    public static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────";
    public static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";

    public static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    public static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    public static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 获取TAG标签
     *
     * @param mTag
     * @param sTag
     * @return
     */
    public static String getFormatTag(String mTag, String sTag) {
        if (!Utils.isEmpty(mTag) && !Utils.equals(mTag, sTag)) {
            return sTag + "-" + mTag;
        }
        return sTag;
    }

    /**
     * 获取栈深度
     *
     * @param trace
     * @return
     */
    public static int getStackOffset(StackTraceElement[] trace) {
        for (int i = LogFormat.MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
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
    public static String getStackInfo(StackTraceElement stack, String level) {
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
     * 获取标准换行信息
     *
     * @param priority
     * @param tag
     * @param message
     * @return
     */
    public static String getFormatLog(int priority, String tag, String message) {
        String time = DateFormat.getDateTimeInstance().format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append(time)
                .append(' ')
                .append(tag)
                .append("(")
                .append(Utils.getLevel(priority))
                .append("): ")
                .append(message)
                .append("\r\n");
        return builder.toString();
    }
}
