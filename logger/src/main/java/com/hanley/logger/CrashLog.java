package com.hanley.logger;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

public class CrashLog implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final CrashCall callback;

    private Thread.UncaughtExceptionHandler mHandler;

    public CrashLog(Context context, CrashCall callback) {
        this.context = context;
        this.callback = callback;
    }

    public void start() {
        this.mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.c("Error:", e);
        if (mHandler != null && e == null) {
            mHandler.uncaughtException(t, e);
        } else {
            handleException();
        }
    }

    private void handleException() {
        if (callback != null) {
            callback.handle();
        } else {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(context, "程序出现异常，即将退出。", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }
}