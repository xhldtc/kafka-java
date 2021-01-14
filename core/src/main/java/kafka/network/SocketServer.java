package kafka.network;

import kafka.server.KafkaConfig;

import java.util.Map;

public class SocketServer {

    private KafkaConfig config;

    private Map<Object, Object> endpoints = config.listeners;
    private int numProcessorThreads = config.numNetworkThreads;
    private int maxQueuedRequests = config.queuedMaxRequests;
    private int totalProcessorThreads = numProcessorThreads * endpoints.size();

    /**
     * 所有endpoints共享同一个RequestChannel, response queue的数量是totalProcessorThreads
     */
    public RequestChannel requestChannel;

    public SocketServer(KafkaConfig config) {
        this.config = config;
        requestChannel = new RequestChannel(totalProcessorThreads, maxQueuedRequests);
    }

    public void startup() {
    }
}
