package org.apache.kafka.common.utils;

public class Utils {

    public static Thread daemonThread(String name, Runnable runnable) {
        return newThread(name, runnable, true);
    }

    public static Thread newThread(String name, Runnable runnable, boolean daemon) {
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(daemon);
        thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        return thread;
    }
}
