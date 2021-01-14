package kafka.server;

import java.util.Properties;

public class KafkaServerStartable {

    public static KafkaServerStartable fromProps(Properties serverProps) {
        return new KafkaServerStartable();
    }

    public void startup() {
        System.out.println("kafka started!!!");
    }

    public void shutdown() {

    }

    public void setServerState(Byte newState) {

    }

    public void awaitShutdown() {

    }
}
