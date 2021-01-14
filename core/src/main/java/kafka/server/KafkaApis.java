package kafka.server;

import kafka.network.RequestChannel;
import org.apache.kafka.common.protocol.ApiKeys;

public class KafkaApis {

    public void handle(RequestChannel.Request request) {
        try {
            switch (ApiKeys.forId(request.requestId)) {
                case PRODUCE:
                    handleProducerRequest(request);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void handleProducerRequest(RequestChannel.Request request) {
    }
}
