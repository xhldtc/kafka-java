package org.apache.kafka.common.network;

public interface Selectable {

    int USE_DEFAULT_BUFFER_SIZE = -1;

    void wakeup();
}
