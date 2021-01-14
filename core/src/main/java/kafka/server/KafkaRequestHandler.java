package kafka.server;

import kafka.network.RequestChannel;

public class KafkaRequestHandler implements Runnable {

    private RequestChannel requestChannel;
    private KafkaApis apis;

    public KafkaRequestHandler(RequestChannel requestChannel) {
        this.requestChannel = requestChannel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                RequestChannel.Request req = null;
                while (req == null) {
                    req = requestChannel.receiveRequest(300);
                }
                apis.handle(req);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
