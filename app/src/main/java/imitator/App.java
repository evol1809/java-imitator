package imitator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import imitator.api.ApiHandler;
import imitator.application.Imitator;
import imitator.common.config.*;
import imitator.common.config.connector.TcpServerConfig;
import imitator.common.exception.CriticalRuntimeException;
import imitator.threadfactory.MainThreadFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

import static java.lang.System.exit;

public class App {

    static Logger logger = Logger.getLogger(App.class.getName());

    public static ImitatorList parseConfigFile(String filePath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        ImitatorList imitators = null;

        // Absolute path or resource path
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             InputStream in = (inputStream != null) ? inputStream : new FileInputStream(filePath)) {

            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            imitators = om.readValue(in, ImitatorList.class);
        } catch (FileNotFoundException ex) {
            logger.severe(filePath + " (No such file or directory)");
            exit(1);
        }

//        logger.info("ConfigList info " + imitators.toString());
        return imitators;
    }

    public static void main(String[] args) throws IOException {

        initLogger();

        try {
            if(args.length == 2) {
                if(!args[0].equals("--configfile")) {
                    logger.severe("Argument not found. Use --help");
                    exit(1);
                }

                ImitatorList imitators = parseConfigFile(args[1]);

                initSystemProperties(imitators.getDefaultTcpServer());

                MainThreadFactory executor = new MainThreadFactory();
                ApiHandler apiHandler = ApiHandler.build(imitators.getShell(), imitators);

                for (ImitatorConfig config :
                        imitators.getImitators()) {
                    executor.newThread(new Imitator(config, apiHandler)).start();
                }

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        System.out.println("SIGTERM");
                        apiHandler.exit();
                    }));

                Thread apiHandlerThread = new Thread(apiHandler);
                apiHandlerThread.setName(imitators.getShell() ? "Shell" : "ApiHandler");
                apiHandlerThread.start();

            } else if(args.length == 1) {
                if (args[0].equals("--help")) {
                    logger.info("Run app with args '--configfile filepath'");
                }
            } else {
                logger.severe("Argument not found. Use --help");
                exit(1);
            }
        } catch (CriticalRuntimeException ex) {
            logger.severe(ex.getMessage());
        }
//        exit(0);
    }

    private static void initSystemProperties(TcpServerConfig defaultTcpServer) {
        Properties prop = System.getProperties();
        prop.put("DEFAULT_TCP_SERVER_PORT", String.valueOf(defaultTcpServer.getPort()));
        prop.put("CONNECTING_TIMEOUT_MILLIS", String.valueOf(defaultTcpServer.getConnectingTimeoutMillis()));
    }

    public static void initLogger() {
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%1$-10s %2$-3s %3$-40s %4$-16s]: %5$-10s \n",
                        new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(record.getMillis())), //1
                        record.getThreadID(), //2
                        getThreadName(record), //3
                        record.getSourceMethodName(), //4
                        formatMessage(record)); //5
            }

            private String getThreadName(LogRecord record) {
                String threadName = "";
                for (Thread t : Thread.getAllStackTraces().keySet())
                    if (t.getId() == record.getThreadID()) threadName = t.getName();
                return threadName;
            }
        };

        try {
            byte[] bytes = ("java.util.logging.FileHandler.count = 1").getBytes();
            LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(bytes));
        } catch (SecurityException | IOException ex) {
            ex.printStackTrace();
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        try {
            Handler fileHandler = new FileHandler(
                    "logger.log"
                    , 200000
                    , 5);
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (SecurityException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class Log {
        public static void createThread(Thread parent, Thread child) {
            logger.info(String.format("The thread '%1$-1s' will start thread%2$-1s '%3$-1s'."
                    , parent.getName()
                    , child.isDaemon() ? "(daemon)" : ""
                    , child.getName())
            );
        }

    }
}
