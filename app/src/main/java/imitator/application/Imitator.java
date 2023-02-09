package imitator.application;


import imitator.App;
import imitator.ImitatorCustomRunnable;
import imitator.api.ApiHandler;
import imitator.common.config.ImitatorConfig;
import imitator.common.config.connector.TcpServerConfig;
import imitator.common.validation.ImitatorConfigValidation;
import imitator.connector.*;
import imitator.common.exception.CriticalRuntimeException;
import imitator.repository.MessageFileRepository;
import imitator.threadfactory.MainThreadFactory;
import imitator.threadfactory.UncaughtExceptionHandler;
import imitator.service.*;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Imitator implements ImitatorCustomRunnable {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private final Object monitor = new Object();

    private final java.lang.Runnable barrierAction
            = () ->
            logger.log(Level.INFO, "Services are ready to start ...");

    private CyclicBarrier barrier;

    protected final List<Connector> connectors = new ArrayList<>();

    protected final MessageFileRepository messageFileRepository;

    protected final ImitationService imitationService;

    private final List<Thread> services = new ArrayList<>();

    private final MainThreadFactory threadFactory = new MainThreadFactory();

    private final ImitatorConfig config;

    public Imitator(ImitatorConfig config, ApiHandler apiHandler) {
        ImitatorConfigValidation.valid(config);
        this.config = config;
//        logger.info(config.toString());
        apiHandler.addApiCommands(this.getApiCommand(), config.getName());
        uncaughtExceptionHandler = new UncaughtExceptionHandler(this.getApiCommand(), getName());

        this.messageFileRepository = new MessageFileRepository(config.getRepository(), config.getProtocol());

        for (TcpServerConfig tcpServerConfig : config.getConnector().getTcpServerList())
            connectors.add(new Connector(tcpServerConfig, threadFactory, uncaughtExceptionHandler));

        imitationService = new ImitationService.Builder()
                .setRepository(messageFileRepository)
                .setRepeat(config.getRepeat())
                // TODO: fix
                .build(connectors.stream()
                                .filter(Objects::nonNull)
                                .map(i -> (Sendable)i)
                                .collect(Collectors.toList())
                        , config.getProtocol().getType());
    }

    @Override
    public String getName() {
        return this.config.getName();
    }

    private final UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    private int findBarrierCount() {
        int countTcpServer = config.getConnector().getTcpServerList() != null
                ? config.getConnector().getTcpServerList().size() : 0;

        return countTcpServer
                + 1 // EastImitationService
                + 1; // FileRepository
    }

    private void makeRepository() {

        services.add(threadFactory.newThread(new ImitatorCustomRunnable() {

            @Override
            public UncaughtExceptionHandler getUncaughtExceptionHandler() {
                return uncaughtExceptionHandler;
            }

            @Override
            public String getName() {
                return Thread.currentThread().getName() + ":MessageFileRepository";
            }

            @Override
            public void run() {
                    messageFileRepository.load();
                try {
                    barrier.await();
                    logger.log(Level.INFO, "The service 'MessageFileRepository' is running ...");
                } catch (InterruptedException ex) {
                    logger.info("interrupted");
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException ex) {
                    if (Thread.currentThread().isInterrupted())
                        Thread.currentThread().interrupt();
                    else
                        throw new RuntimeException(ex);
                }
            }
        }));
    }

    private void makeConnector() {

        for (Connector connector : connectors) {
            services.add(threadFactory.newThread(new ImitatorCustomRunnable() {

                @Override
                public UncaughtExceptionHandler getUncaughtExceptionHandler() {
                    return uncaughtExceptionHandler;
                }

                @Override
                public String getName() {
                    return Thread.currentThread().getName() + ":TcpServer";
                }

                @Override
                public void run() {
                    connector.start();
                    try {
                        barrier.await();
                        logger.log(Level.INFO, "The service 'TcpServer' is running ...");
                    } catch (InterruptedException ex) {
                        logger.info("interrupted");
                        Thread.currentThread().interrupt();
                    } catch (BrokenBarrierException ex) {
                        if (Thread.currentThread().isInterrupted())
                            Thread.currentThread().interrupt();
                        else
                            throw new RuntimeException(ex);
                    }
                }
            }));
        }
    }

    private void makeEastImitationService() {
        services.add(threadFactory.newThread( new ImitatorCustomRunnable() {

            @Override
            public UncaughtExceptionHandler getUncaughtExceptionHandler() {
                return uncaughtExceptionHandler;
            }

            @Override
            public String getName() {
                return Thread.currentThread().getName() + ":" + imitationService.getName();
            }

            @Override
            public void run() {
                logger.log(Level.INFO, "Preparing service '" + imitationService.getName() + "' ...");
                try {
                    barrier.await();
                    logger.log(Level.INFO, "The service '" + imitationService.getName() + "' is running ...");
                } catch (InterruptedException ex) {
                    logger.info("interrupted");
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException ex) {
                    if (Thread.currentThread().isInterrupted())
                        Thread.currentThread().interrupt();
                    else
                        throw new RuntimeException(ex);
                }

                imitationService.send();
            }
        }));
    }

    @Override
    public void run() {

        synchronized (this.monitor) {
            if (this.isStarted()) {
                logger.warning("Imitator already started");
                return;
            }

            int partiesCount = findBarrierCount();
            barrier = new CyclicBarrier(partiesCount, barrierAction);
            makeConnector();
            makeRepository();
            makeEastImitationService();
            if (services.size() != partiesCount)
                throw new CriticalRuntimeException("The services count must to be equals barrier parties count");

            logger.log(Level.INFO, "Starting services...");

            for (Thread thread : services) {
                App.Log.createThread(Thread.currentThread(), thread);
                thread.start();
            }
        }

        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isStarted())
                    System.out.println(getName() + " stopped");
            }}, 1000);
    }

    public void stop() {
        synchronized (this.monitor) {

            for (Connector connector : connectors)
                connector.getTcpServer().stop();

            for (Thread thread : this.services)
                thread.interrupt();

            uncaughtExceptionHandler.stopCheckerTimer();
        }
    }

    public boolean isStarted() {
        synchronized (this.monitor) {
            return services.stream().anyMatch(Thread::isAlive);
        }
    }

    private ApiCommands apiCommands;

    public ApiCommands getApiCommand() {
        if(apiCommands == null)
            apiCommands = new ApiCommands(this);
        return apiCommands;
    }
    public interface StopCommand {
        void stop();
    }

    public class ApiCommands implements StopCommand {
        private final Imitator imitator;

        public ApiCommands(Imitator imitator) {
            this.imitator = imitator;
        }

        @Override
        public void stop() {
            imitator.stop();
        }

        public boolean isStarted() {
            return imitator.isStarted();
        }
    }

}
