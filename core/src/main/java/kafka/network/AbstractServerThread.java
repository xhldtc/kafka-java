package kafka.network;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServerThread implements Runnable {

    private AtomicBoolean alive = new AtomicBoolean(true);

    protected boolean isRunning() {
        return alive.get();
    }
}
