package imitator.common.utils.tcpserver;


import imitator.App;
import imitator.ImitatorCustomRunnable;
import imitator.application.Imitator;
import imitator.common.exception.CriticalRuntimeException;
import imitator.threadfactory.UncaughtExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link Imitator} TcpServer.
 * The TcpServer for data exchange with external connections
 */
public class TcpServer {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private static final int DEFAULT_PORT = Integer.parseInt( System.getProperty("DEFAULT_TCP_SERVER_PORT") );

    private static final int CONNECTING_TIMEOUT = Integer.parseInt( System.getProperty("CONNECTING_TIMEOUT_MILLIS") );

    private ServerSocket serverSocket;

    private final ExecutorService executor = Executors.newCachedThreadPool(new WorkerThreadFactory());

    private final List<Connection> connections = new ArrayList<>();

    private final ThreadFactory threadFactory;

    private final UncaughtExceptionHandler uncaughtExceptionHandler;

    private int port;

    private Thread listenThread;

    private final Object monitor = new Object();

    /**
     * Create a new {@link TcpServer} listening on the default port
     *
     */
    public TcpServer(int port, ThreadFactory threadFactory, UncaughtExceptionHandler uncaughtExceptionHandler)  {
        this.threadFactory = threadFactory;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.port = port;
        if(this.port == 0)
            this.port = DEFAULT_PORT;
    }

    /**
     * Start the tcp server and accept incoming connections.
     */
    public void start() {
        logger.log(Level.INFO, "Preparing TcpServer ...");

        synchronized (this.monitor) {
            if (isStarted()) throw new CriticalRuntimeException("TcpServer already started");
            try {
                this.serverSocket = new ServerSocket(this.port);
                logger.info(getName() + " listening port " + this.port);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            this.listenThread = this.threadFactory.newThread(new ImitatorCustomRunnable() {
                @Override
                public UncaughtExceptionHandler getUncaughtExceptionHandler() {
                    return uncaughtExceptionHandler;
                }

                @Override
                public String getName() {
                    return Thread.currentThread().getName() + ":acceptConnections";
                }

                @Override
                public void run() {
                    acceptConnections();
                }
            });
            this.listenThread.setName(Thread.currentThread().getName() + ":Listener");
            this.listenThread.setDaemon(true);
            App.Log.createThread(Thread.currentThread(), this.listenThread);
            this.listenThread.start();
        }
    }

    public String getName() {
        return "TcpServer";
    }

    void acceptConnections() {
        do {
            try {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(CONNECTING_TIMEOUT);
                executor.execute(new ConnectionHolder(socket));
            } catch (SocketTimeoutException e) {
                // Ignore
            } catch(SocketException ex) {
                // Ignore
            } catch (Exception ex) {
                logger.log(Level.INFO, "TcpServer error: " + ex
                        + ". The TcpServer will continue to accept new connections.");
            }
        }
        while (!this.serverSocket.isClosed());
        logger.info(TcpServer.class.getSimpleName() + " acceptConnections [end]");
    }

    /**
     * Return if the server has been started.
     *
     * @return {@code true} if the server is running
     */
    public boolean isStarted() {
        synchronized (this.monitor) {
            return this.listenThread != null;
        }
    }

    /**
     * Gracefully stop the TcpServer.
     */
    public void stop() {
        synchronized (this.monitor) {
            if (this.listenThread == null)
                return;

            try {
                closeAllConnections();
            } catch (IOException ex) {
                logger.info("TcpServer error: the connections could not be terminated " +
                        "and will be killed. Message: " + ex.getMessage());
            }
// TODO: try/catch -> lombok
            try {
                this.executor.shutdown();
                if(!this.executor.awaitTermination(1, TimeUnit.MINUTES))
                    logger.info("The connection executor timeout has ended");
            } catch (RuntimeException ex) {
                logger.log(Level.INFO
                        , "TcpServer error: the connections did not wait for the correct shutdown " +
                                "and will be killed. Message: " + ex.getMessage());
            } catch (InterruptedException ex) {
                logger.log(Level.INFO
                        , "TcpServer error: the connections did not wait for the correct shutdown " +
                                "and will be killed.");
            }

            try {
                this.serverSocket.close();
            } catch (IOException ex) {
                logger.info("TcpServer error: the server socket could not be terminated " +
                        "and will be killed. Message: " + ex.getMessage());
            }

            try {
                this.listenThread.join(1000);
            } catch (RuntimeException ex) {
                logger.info("TcpServer error: the Listener cannot shutdown correctly: " + ex.getMessage());
            } catch (InterruptedException ex) {
                logger.log(Level.INFO
                        , "TcpServer error: the Listener did not wait for the correct shutdown " +
                                "and will be killed.");
            }
            this.listenThread = null;
            this.serverSocket = null;
        }
        logger.info(TcpServer.class.getSimpleName() + " [end]");
    }

    private void closeAllConnections() throws IOException {
        synchronized (this.connections) {
            for (Connection connection : this.connections) {
                connection.close();
            }
            logger.info(TcpServer.class.getSimpleName() + " closeAllConnections [end]");
        }
    }

    /**
     * Add a new message to send.
     *
     * @param msg message
     */
    public void send(byte[] msg) {
        synchronized (this.monitor) {
            //TODO: delete synchronized (connections). read lock/unlock
            synchronized (this.connections) {
                this.connections.forEach((i) -> i.addSendingQueue(msg));
            }
        }
    }

    private void addConnection(Connection connection) {
        synchronized (this.connections) {
            this.connections.add(connection);
            logger.info("A connection (" + connection.getPort() + ") has been created");
        }
    }

    private void removeConnection(Connection connection) {
        synchronized (this.connections) {
            this.connections.remove(connection);
            logger.info("A connection (" + connection.getPort() + ") has been deleted");
        }
    }

    /**
     * Factory method used to create the {@link Connection}.
     *
     * @param socket       the source socket
     * @param inputStream  the socket input stream
     * @param outputStream the socket output stream
     * @return a connection
     * @throws IOException in case of I/O errors
     */
    Connection createConnection(Socket socket, InputStream inputStream, OutputStream outputStream)
            throws IOException {
        return new Connection(socket, inputStream, outputStream);
    }

    /**
     * {@link Runnable} to handle a single connection.
     *
     * @see Connection
     */
    class ConnectionHolder implements Runnable {

        Socket socket;

        ConnectionHolder(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                handle();
            } catch (ConnectionClosedException ex) {
                // Ignore
            } catch (SocketException ex) {
                // Ignore
            } catch (Exception ex) {
                logger.log(Level.INFO, "TcpServer error: " + ex);
            }
        }

        private void handle() throws IOException, InterruptedException {
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()) {
                Connection connection = createConnection(socket, inputStream, outputStream);
                runConnection(connection);
            }
            socket.close();
        }

        private void runConnection(Connection connection) throws IOException, InterruptedException {
            try {
                addConnection(connection);
                connection.run();
            } finally {
                removeConnection(connection);
            }
        }
    }

    /**
     * {@link ThreadFactory} to create the worker threads.
     */
    private static class WorkerThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName(Thread.currentThread().getName() + ":Connection-" + this.threadNumber.getAndIncrement());
            App.Log.createThread(Thread.currentThread(), thread);
            return thread;
        }
    }
}