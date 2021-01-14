package kafka.server;

import kafka.network.RequestChannel;
import org.apache.kafka.common.utils.Utils;

import java.util.TreeMap;

public class KafkaRequestHandlerPool {

    private int brokerId;
    private RequestChannel requestChannel;
    private KafkaApis apis;
    private int numThreads;

    private Thread[] threads;
    private KafkaRequestHandler[] runnables;

    public KafkaRequestHandlerPool(RequestChannel requestChannel, KafkaApis apis,
                                   int numThreads) {
        threads = new Thread[numThreads];
        runnables = new KafkaRequestHandler[numThreads];
        for(int i=0;i<numThreads;i++) {
            runnables[i] = new KafkaRequestHandler(requestChannel);
            threads[i] = Utils.daemonThread("kafka-request-handler-" + i, runnables[i]);
            threads[i].start();
        }
    }

}
