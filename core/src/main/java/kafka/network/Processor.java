package kafka.network;

import org.apache.kafka.common.network.Selector;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Processor extends AbstractServerThread {

    static class ConnectionId {
        private String localHost;
        private int localPort;
        private String remoteHost;
        private int remotePort;

        ConnectionId(String localHost, int localPort, String remoteHost, int remotePort) {
            this.localHost = localHost;
            this.localPort = localPort;
            this.remoteHost = remoteHost;
            this.remotePort = remotePort;
        }

        @Override
        public String toString() {
            return String.format("%s:%d-%s:%d", localHost, localPort, remoteHost, remotePort);
        }
    }

    private ConcurrentLinkedQueue<SocketChannel> newConnections = new ConcurrentLinkedQueue<>();

    private Selector selector = new Selector();

    @Override
    public void run() {
        while (isRunning()) {
            try {
                configureNewConnections();
                processNewResponses();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果server有response，就发送返回给client
     */
    private void processNewResponses(){

    }


    void accept(SocketChannel socketChannel) {
        newConnections.add(socketChannel);
        wakeup();
    }

    /**
     * 如果阻塞队列里有连接，就取出并把连接的read事件注册到processor自有的selector上，每个processor一个selector
     */
    private void configureNewConnections() {
        while (!newConnections.isEmpty()) {
            SocketChannel channel = newConnections.poll();
            try {
                String localHost = channel.socket().getLocalAddress().getHostAddress();
                int localPort = channel.socket().getLocalPort();
                String remoteHost = channel.socket().getInetAddress().getHostAddress();
                int remotePort = channel.socket().getPort();
                String connectionId = new ConnectionId(localHost, localPort, remoteHost,remotePort).toString();
                selector.register(connectionId, channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void wakeup() {
        selector.wakeup();
    }


}
