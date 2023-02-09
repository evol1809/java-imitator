package imitator.api;


import imitator.App;
import imitator.application.Imitator;
import imitator.common.config.ImitatorList;
import imitator.threadfactory.MainThreadFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class ApiHandler implements Runnable {

    protected static final Logger logger = Logger.getLogger(App.class.getName());

    protected ApiHandler() {}

    protected static ApiHandler apiHandler;

    protected boolean isStop = false;

    MainThreadFactory threadFactory = new MainThreadFactory();

    protected ImitatorList imitators;

    public static ApiHandler build(boolean isShell, ImitatorList imitators) {
        if(apiHandler == null && isShell)
            apiHandler = new Shell();
        else
            apiHandler = new ApiHandler();

        apiHandler.imitators = imitators;
        return apiHandler;
    }

    ConcurrentHashMap<String, Imitator.ApiCommands> map = new ConcurrentHashMap<>();

    public void addApiCommands(Imitator.ApiCommands apiCommands, String imitatorName) {
        map.put(imitatorName, apiCommands);
    }

    @Override
    public void run() {}

    public void stop() {
        for(Map.Entry<String, Imitator.ApiCommands> pair : map.entrySet()) {
            if(pair.getValue().isStarted()) {
                pair.getValue().stop();
                waitExecution();
            } else {
                System.out.println("Imitator " + pair.getKey() + " already stopped");
            }
        }
    }

    public void start() {
        for(Map.Entry<String, Imitator.ApiCommands> pair : map.entrySet()) {
            if(!pair.getValue().isStarted()) {
                threadFactory.newThread(new Imitator(imitators.getImitators().stream()
                        .filter(i -> i.getName().equals(pair.getKey()))
                        .findFirst().orElseThrow(RuntimeException::new)
                        , this)).start();
            } else {
                System.out.println("Imitator " + pair.getKey() + " already started");
            }
        }
        waitExecution();
    }

    void startImitator(String name) {
        Imitator.ApiCommands apiCommands = map.entrySet().stream()
                .filter(i -> i.getKey().equals(name))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getValue();

        if(!apiCommands.isStarted()) {
            threadFactory.newThread(new Imitator(imitators.getImitators().stream()
                    .filter(i -> i.getName().equals(name))
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    , this)).start();
            waitExecution();
        } else {
            System.out.println("Imitator " + name + " already started");
        }
    }

    void stopImitator(String name) {
        Imitator.ApiCommands apiCommands = map.entrySet().stream()
                .filter(i -> i.getKey().equals(name))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getValue();

        if (apiCommands.isStarted()) {
            apiCommands.stop();
            waitExecution();
        } else {
            System.out.println("Imitator " + name + " already stopped");
        }
    }


    void waitExecution() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit() {
        stop();
        isStop = true;
    }
}
