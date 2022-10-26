package com.liuhanling.logger;

import android.os.Environment;

import java.io.File;

public final class Config {

    public static final String LOG_TAG = "LOGGER";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String LOG_PATH = ROOT_PATH + File.separator + "logger";
    public static final int LOG_FILE_SIZE = 20 * 1024 * 1024;
    public static final int LOG_LINE_SIZE = 5 * 1024;
    // Add Log Level
    public static final int CRASH = 8;

}