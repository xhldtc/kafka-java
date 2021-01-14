package org.apache.kafka.common.network;


import kafka.network.Processor;
import org.apache.kafka.common.KafkaException;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Selector implements Selectable {

    private final java.nio.channels.Selector nioSelector;

    public Selector() {
        try {
            this.nioSelector = java.nio.channels.Selector.open();
        } catch (IOException e) {
            throw new KafkaException(e);
        }
    }

    public void register(String id, SocketChannel socketChannel) throws ClosedChannelException {
        SelectionKey key = socketChannel.register(nioSelector, SelectionKey.OP_READ);
        //将一个自定义对象attach到这个key上
        key.attach(new Object());
    }

    /**
     * 如果有其他线程持有nioSelector并且阻塞IO在select方法上，wakeup会让select立即返回
     */
    @Override
    public void wakeup() {
        this.nioSelector.wakeup();
    }

}
