package Management;

import Windows.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class ServerCheck extends Thread{

    private final int port;
    private final long time;
    public boolean connect = false;

    public ServerCheck(int port, int time) {
        this.port = port;
        this.time = time;
    }

    @Override
    public void run() {
        Timeout timeout = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            timeout = new Timeout(serverSocket, time);
            timeout.start();
            serverSocket.accept();
            if (serverSocket.isBound()) {
                timeout.interrupt();
                serverSocket.close();
                connect = true;
                new Server(port);
            } else interrupt();
            serverSocket.close();
        } catch (SocketException e) {
            assert timeout != null;
            timeout.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // connect timer
    class Timeout extends Thread {
        private long time;
        private final ServerSocket socket;

        Timeout(ServerSocket socket, long time) {
            this.time = time;
            this.socket = socket;
        }

        @Override
        public void run() {
            long timer = System.currentTimeMillis() + time * 1000;
            while (!isInterrupted()) {
                if (timer < System.currentTimeMillis())
                    try {
                        socket.close();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}


