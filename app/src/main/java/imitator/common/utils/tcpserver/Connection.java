package imitator.common.utils.tcpserver;



import imitator.App;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * A {@link TcpServer} connection.
 */
public class Connection {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    private final Socket socket;

    private final InputStream inputStream;

    private final OutputStream outputStream;

    private final BlockingQueue<byte[]> sendingQueue = new LinkedBlockingQueue<>();

    private volatile boolean running = true;

    /**
     * Create a new {@link Connection} instance.
     * @param socket the source socket
     * @param inputStream the socket input stream
     * @param outputStream the socket output stream
     * @throws IOException in case I/O errors
     */
    Connection(Socket socket, InputStream inputStream, OutputStream outputStream) throws IOException {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Run the connection.
     * @throws IOException in case of errors
     */
    void run() throws IOException, InterruptedException {

        while (this.running) {
            Thread.sleep(10);
            receive();
            send();
        }
    }

    private boolean send() throws IOException {

        if(sendingQueue.size() > 0) {
            byte[] msg = sendingQueue.poll();
            if (msg.length > 0) {
                outputStream.write(msg);
            }
        }
        return true;
    }

    /**
     * Add a new message to send.
     * @param msg message
     */
    void addSendingQueue(byte[] msg) { sendingQueue.offer(msg);  }

    private void receive() throws IOException {
        // not used now, so just reads. TODO: add read processing 
            if(this.inputStream.available() > 0) {
                byte[] buf = new byte[this.inputStream.available()];
                this.inputStream.read(buf);
            }
    }

    /**
     * Close the connection.
     * @throws IOException in case of I/O errors
     */
    public void close() throws IOException {
        this.running = false;
        this.socket.close();
    }

    public int getPort() {
        return this.socket.getPort();
    }
}
