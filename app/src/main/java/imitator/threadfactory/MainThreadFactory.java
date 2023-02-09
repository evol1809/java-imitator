package imitator.threadfactory;


import imitator.ImitatorCustomRunnable;
import imitator.common.exception.CriticalRuntimeException;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainThreadFactory implements ThreadFactory {
    private static final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

    public MainThreadFactory() {}

    @Override
    public Thread newThread(Runnable run) {

        if (run instanceof ImitatorCustomRunnable) {
            Thread thread = defaultFactory.newThread(run);
            thread.setName(((ImitatorCustomRunnable) run).getName());
            thread.setUncaughtExceptionHandler(((ImitatorCustomRunnable) run).getUncaughtExceptionHandler());
            return thread;
        }

        throw new CriticalRuntimeException(
                "MainThreadFactory:newThread must accept ImitatorCustomRunnable");
    }
}

