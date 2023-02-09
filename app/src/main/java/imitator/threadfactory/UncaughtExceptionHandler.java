package imitator.threadfactory;


import imitator.App;
import imitator.application.Imitator;
import imitator.common.exception.CriticalRuntimeException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private Timer checkerTimer;

    class ThreadException {
        final Throwable exception;
        final String threadName;

        public ThreadException(Throwable exception, String threadName) {
            this.exception = exception;
            this.threadName = threadName;
        }

        @Override
        public String toString() {
            if (exception instanceof CriticalRuntimeException)
                return "Critical error: " + threadName + " " + exception.getMessage();

            return "Unknown critical error: " + threadName + " " + exception.toString();
        }
    }

    Collection<ThreadException> exceptions = Collections.synchronizedCollection(new ArrayList<>());

    final Imitator.StopCommand stopCommand;

    private final String imitatorName;

    public UncaughtExceptionHandler(Imitator.StopCommand stopCommand, String imitatorName) {
        this.imitatorName = imitatorName;
        this.stopCommand = stopCommand;
        checkingForNewExceptions();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
            ThreadException threadException = new ThreadException(t, thread.getName());
            logger.log(Level.INFO, threadException.toString());
            exceptions.add(threadException);
    }

    void checkingForNewExceptions() {
        TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                if (exceptions.size() > 0) {
                    logger.log(Level.INFO, "The application has a critical error and will be stopped.");

                    try {
                        stopCommand.stop();
                    } catch (Exception e) {
                        logger.info(Thread.currentThread().getName()
                                + " NewExceptionsChecker accepted exception:" + e.getMessage());
                    }
                }
            }
        };
        checkerTimer = new Timer(true);
        checkerTimer.schedule(tt2, 0, 1000);
    }

    public void stopCheckerTimer() {
        checkerTimer.cancel();
    }
}
