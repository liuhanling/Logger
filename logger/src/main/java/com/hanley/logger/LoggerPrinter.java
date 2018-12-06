package com.hanley.logger;

import android.content.Context;
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

public class LoggerPrinter implements Printer {

    private static final int JSON_INDENT = 2;

    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final Config config;

    public LoggerPrinter(Context context) {
        this(LogConfig.Builder().build());
    }

    public LoggerPrinter(Config config) {
        this.config = Utils.checkNotNull(config);
    }

    @Override
    public Printer t(String tag) {
        if (tag != null) {
            localTag.set(tag);
        }
        return this;
    }

    @Override
    public void v(String message, Object... args) {
        log(Log.VERBOSE, null, message, args);
    }

    @Override
    public void d(String message, Object... args) {
        log(Log.DEBUG, null, message, args);
    }

    @Override
    public void d(Object object) {
        log(Log.DEBUG, null, Utils.toString(object));
    }

    @Override
    public void i(String message, Object... args) {
        log(Log.INFO, null, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        log(Log.WARN, null, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        log(Log.ERROR, null, message, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        log(Log.ERROR, throwable, message, args);
    }

    @Override
    public void a(String message, Object... args) {
        log(Log.ASSERT, null, message, args);
    }

    @Override
    public void json(String json) {
        if (Utils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
                return;
            }
            e("Invalid Json");
        } catch (JSONException e) {
            e("Invalid Json");
        }
    }

    @Override
    public void xml(String xml) {
        if (Utils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e("Invalid xml");
        }
    }

    @Override
    public synchronized void log(int priority, String tag, String message, Throwable throwable) {
        if (throwable == null && Utils.isEmpty(message)) {
            message = "Empty/NULL log message";
        } else if(throwable != null && Utils.isEmpty(message)) {
            message = Log.getStackTraceString(throwable);
        } else if (throwable != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        config.log(priority, tag, message);
    }

    private synchronized void log(int priority, Throwable throwable, String msg, Object... args) {
        Utils.checkNotNull(msg);
        String tag = getTag();
        String message = Utils.isEmpty(args) ? msg : String.format(msg, args);
        log(priority, tag, message, throwable);
    }

    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        return null;
    }
}
