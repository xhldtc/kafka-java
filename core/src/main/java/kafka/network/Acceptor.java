package kafka.network;

import kafka.common.KafkaException;
import org.apache.kafka.common.network.Selectable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Acceptor extends AbstractServerThread {

    private final java.nio.channels.Selector nioSelector = java.nio.channels.Selector.open();
    final ServerSocketChannel serverChannel = openServerSocket("", 123);

    private int sendBufferSize;
    private int recvBufferSize;
    private Processor[] processors;

    public Acceptor(int sendBufferSize, int recvBufferSize, int brokerId,
                    Processor[] processors) throws IOException {
        this.sendBufferSize = sendBufferSize;
        this.recvBufferSize = recvBufferSize;
        this.processors = processors;
    }

    /**
     * 无限循环接收Accept类型的key，平均分配给Processor数组处理
     */
    @Override
    public void run() {
        try {
            serverChannel.register(nioSelector, SelectionKey.OP_ACCEPT);
            int currentProcessor = 0;
            while (isRunning()) {
                try {
                    int ready = nioSelector.select(500);
                    if (ready > 0) {
                        Set<SelectionKey> keys = nioSelector.selectedKeys();
                        Iterator<SelectionKey> iter = keys.iterator();
                        while (iter.hasNext() && isRunning()) {
                            try {
                                SelectionKey key = iter.next();
                                iter.remove();
                                if (key.isAcceptable()) {
                                    accept(key, processors[currentProcessor]);
                                } else {
                                    throw new IllegalStateException("Unrecognized key state for acceptor thread.");
                                }
                                currentProcessor = (currentProcessor + 1) % processors.length;
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable e) {
                    //源码这里先catch ControlThrowable, catch之后会抛出，因为ControlThrowable是必须被传播出去的异常
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    /**
     * 初始化server socket并绑定到server channel上，设置receive buffer size
     *
     * @param host
     * @param port
     * @return
     * @throws IOException Done
     */
    private ServerSocketChannel openServerSocket(String host, int port) throws IOException {
        InetSocketAddress socketAddress = (host == null || host.trim().isEmpty()) ?
                new InetSocketAddress(port) : new InetSocketAddress(host, port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        if (recvBufferSize != Selectable.USE_DEFAULT_BUFFER_SIZE) {
            serverChannel.socket().setReceiveBufferSize(recvBufferSize);
        }
        try {
            serverChannel.socket().bind(socketAddress);
        } catch (SocketException e) {
            throw new KafkaException(String.format("Socket server failed to bind to %s:%d: %s.",
                    socketAddress.getHostString(), port, e.getMessage()), e);
        }
        return serverChannel;
    }

    /**
     * 将accept到的socket channel交给processor去处理
     * @param key
     * @param processor
     * @throws IOException
     */
    void accept(SelectionKey key, Processor processor) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        try {
            socketChannel.configureBlocking(false);
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setKeepAlive(true);
            if (sendBufferSize != Selectable.USE_DEFAULT_BUFFER_SIZE) {
                socketChannel.socket().setSendBufferSize(sendBufferSize);
            }
            processor.accept(socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
