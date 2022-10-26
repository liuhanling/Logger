package com.liuhanling.logger;

import android.content.Context;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class LogPrinter implements Printer {

    private LogWriter mLogWriter;
    private ThreadLocal<String> mLogTag = new ThreadLocal<>();
    private final LogConfig mLogConfig;

    LogPrinter(Context context) {
        this(LogConfig.builder(context).build());
    }

    LogPrinter(LogConfig config) {
        this.mLogConfig = Utils.checkNotNull(config);
        this.initWriter();
    }

    private void initWriter() {
        if (mLogConfig.writeLog || mLogConfig.crashLog) {
            HandlerThread thread = new HandlerThread("AndroidFileLogger." + mLogConfig.path);
            thread.start();
            mLogWriter = new LogWriter(thread.getLooper(), mLogConfig.path);
        }
        if (mLogConfig.crashLog) {
            CrashLog.getInstance()
                    .init(mLogConfig.context)
                    .setCrashCall(mLogConfig.callback)
                    .start();
        }
    }

    @Override
    public Printer tag(String tag) {
        Utils.checkNotNull(tag);
        mLogTag.set(tag);
        return this;
    }

    @Override
    public void v(Object object) {
        log(Log.VERBOSE, object);
    }

    @Override
    public void v(String message, Object object) {
        log(Log.VERBOSE, message, object);
    }

    @Override
    public void d(Object object) {
        log(Log.DEBUG, object);
    }

    @Override
    public void d(String message, Object object) {
        log(Log.DEBUG, message, object);
    }

    @Override
    public void i(Object object) {
        log(Log.INFO, object);
    }

    @Override
    public void i(String message, Object object) {
        log(Log.INFO, message, object);
    }

    @Override
    public void w(Object object) {
        log(Log.WARN, object);
    }

    @Override
    public void w(String message, Object object) {
        log(Log.WARN, message, object);
    }

    @Override
    public void e(Object object) {
        log(Log.ERROR, object);
    }

    @Override
    public void e(String message, Object object) {
        log(Log.ERROR, message, object);
    }

    @Override
    public void a(Object object) {
        log(Log.ASSERT, object);
    }

    @Override
    public void a(String message, Object object) {
        log(Log.ASSERT, message, object);
    }

    @Override
    public void c(Object object) {
        log(Config.CRASH, object);
    }

    @Override
    public void c(String message, Object object) {
        log(Config.CRASH, message, object);
    }

    @Override
    public void j(String json) {
        j("", json);
    }

    @Override
    public void j(String message, String json) {
        if (Utils.isEmpty(json)) {
            e(message + "Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject object = new JSONObject(json);
                message = Utils.isEmpty(message) ? message : message + '\n';
                i(message + object.toString(2));
                return;
            }
            if (json.startsWith("[")) {
                JSONArray object = new JSONArray(json);
                message = Utils.isEmpty(message) ? message : message + '\n';
                i(message + object.toString(2));
                return;
            }
            e(message + "Invalid Json");
        } catch (JSONException e) {
            e(message, e);
        }
    }

    @Override
    public void x(String xml) {
        x("", xml);
    }

    @Override
    public void x(String message, String xml) {
        if (Utils.isEmpty(xml)) {
            e(message + "Empty/Null xml content");
            return;
        }
        try {
            Source source = new StreamSource(new StringReader(xml));
            StreamResult result = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            String msg = result.getWriter().toString().replaceFirst(">", ">\n");
            message = Utils.isEmpty(message) ? message : message + '\n';
            i(message + msg);
        } catch (TransformerException e) {
            e(message, e);
        }
    }

    @Override
    public void log(int priority, Object object) {
        logFormat(priority, getLogTag(), Utils.toString(object));
    }

    @Override
    public void log(int priority, String message, Object object) {
        String content = Utils.toString(message) + Utils.toString(object);
        logFormat(priority, getLogTag(), content);
    }

    /**
     * 打印信息格式化
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void logFormat(int priority, String tag, String message) {
        if (Utils.isEmpty(message))
            return;

        if (mLogConfig.printLog || mLogConfig.writeLog || mLogConfig.crashLog) {
            tag = LogFormat.getFormatTag(tag, mLogConfig.tag);
            if (mLogConfig.formatLog) {
                printTopper(priority, tag);
                printThread(priority, tag);
                printMethod(priority, tag);
                printMessage(priority, tag, message);
                printBottom(priority, tag);
            } else {
                printMessage(priority, tag, message);
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
        if (mLogConfig.showThread) {
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
        if (mLogConfig.showMethod > 0) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (Utils.isEmpty(trace)) return;

            int count = mLogConfig.showMethod;
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
            // 自动换行
            if (len <= Config.LOG_LINE_SIZE) {
                if (mLogConfig.formatLog) {
                    println(priority, tag, LogFormat.HORIZONTAL_LINE + " " + msg);
                } else {
                    println(priority, tag, msg);
                }
                writeln(priority, tag, msg);
                continue;
            }
            for (int i = 0; i < len; i += Config.LOG_LINE_SIZE) {
                int count = Math.min(len - i, Config.LOG_LINE_SIZE);
                String text = new String(bytes, i, count);
                if (mLogConfig.formatLog) {
                    println(priority, tag, LogFormat.HORIZONTAL_LINE + " " + text);
                } else {
                    println(priority, tag, text);
                }
                writeln(priority, tag, text);
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
        if (mLogConfig.printLog) {
            int level = priority == Config.CRASH ? Log.ERROR : priority;
            Log.println(level, tag, message);
        }
    }

    /**
     * 写入文件
     *
     * @param priority
     * @param tag
     * @param message
     */
    private void writeln(int priority, String tag, String message) {
        if ((mLogConfig.writeLog && priority <= Log.ASSERT) || (mLogConfig.crashLog && priority == Config.CRASH)) {
            message = LogFormat.getFormatLog(priority, tag, message);
            mLogWriter.sendMessage(mLogWriter.obtainMessage(priority, message));
        }
    }

    private String getLogTag() {
        String tag = mLogTag.get();
        if (!Utils.isEmpty(tag)) {
            mLogTag.remove();
        }
        return tag;
    }
}