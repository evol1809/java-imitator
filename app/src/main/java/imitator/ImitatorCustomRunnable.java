package imitator;

import imitator.threadfactory.UncaughtExceptionHandler;

public interface ImitatorCustomRunnable extends Runnable {

    UncaughtExceptionHandler getUncaughtExceptionHandler();

    String getName();
}
