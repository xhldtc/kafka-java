package kafka.server;

import java.util.Map;

public class KafkaConfig {

    public Map<Object, Object> listeners;
    /**
     * 每个endpoint的processor数量
     */
    public int numNetworkThreads;
    public int queuedMaxRequests;
    /**
     * 处理kafka消息的线程数量
     */
    public int numIoThreads;
}
