package com.liuhanling.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

public final class CrashLog implements Thread.UncaughtExceptionHandler {

    private Context mContext;
    private CrashCall mCrashCall;
    private Thread.UncaughtExceptionHandler mCrashHandler;

    private CrashLog() {
    }

    private static final class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final CrashLog INSTANCE = new CrashLog();
    }

    public static CrashLog getInstance() {
        return Holder.INSTANCE;
    }

    public CrashLog init(Context context) {
        this.mContext = context;
        return Holder.INSTANCE;
    }

    public CrashLog setCrashCall(CrashCall crashCall) {
        this.mCrashCall = crashCall;
        return Holder.INSTANCE;
    }

    public void start() {
        this.mCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger.c("Crash:", e);
        // 1、捕获到的异常为NULL，需要交给系统处理
        // 2、捕获到的异常非NULL，需要自己处理异常
        if (this.mCrashHandler != null && e == null) {
            this.mCrashHandler.uncaughtException(t, e);
        } else {
            handleException(e);
        }
    }

    /**
     * 默认处理异常
     */
    private void handleException(final Throwable e) {
        // App进程处于挂掉边缘，已经是未响应状态，因为事件传递已经不起作用了，所以我们需要再激活一个Looper
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序异常，退出重启!", Toast.LENGTH_SHORT).show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mCrashCall != null) {
                            mCrashCall.handleException(e);
                        } else {
                            Process.killProcess(Process.myPid());
                            System.exit(0);
                        }
                    }
                }, 1000);
                Looper.loop();
            }
        });
    }
}