package kafka.server;

import kafka.network.SocketServer;

import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaServer {

    private KafkaConfig config;

    private AtomicBoolean startupComplete = new AtomicBoolean(false);
    private AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private AtomicBoolean isStartingUp = new AtomicBoolean(false);


    private KafkaApis apis;
    private SocketServer socketServer;
    private KafkaRequestHandlerPool requestHandlerPool;

    public KafkaServer(KafkaConfig config) {
        this.config = config;
    }

    public void startup() {
        try {
            if (isShuttingDown.get()) {
                throw new IllegalStateException("Kafka server is still shutting down, cannot re-start!");
            }
            if (startupComplete.get()) {
                return;
            }
            boolean canStartup = isStartingUp.compareAndSet(false, true);
            if (canStartup) {
                //启动服务器监听端口
                socketServer = new SocketServer(config);
                socketServer.startup();

                apis = new KafkaApis();

                requestHandlerPool = new KafkaRequestHandlerPool(socketServer.requestChannel, apis, config.numIoThreads);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }
}
