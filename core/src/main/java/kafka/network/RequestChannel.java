package kafka.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class RequestChannel {

    private List<IntConsumer> responseListeners;
    private ArrayBlockingQueue<Request> requestQueue;
    private List<BlockingQueue<Response>> responseQueues;

    public RequestChannel(int numProcessors, int queueSize) {
        responseListeners = new ArrayList<>();
        requestQueue = new ArrayBlockingQueue<>(queueSize);
        responseQueues = new ArrayList<>(numProcessors);
        for (int i = 0; i < numProcessors; i++) {
            responseQueues.add(new LinkedBlockingQueue<>());
        }
    }

    public Request receiveRequest(long timeout) throws InterruptedException {
        return requestQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public static class Request {

        public short requestId;

        private ByteBuffer buffer;

        Request(ByteBuffer buffer) {
            this.buffer = buffer;
            requestId = buffer.getShort();
        }
    }

    static class Response {

    }
}
