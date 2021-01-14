package kafka.common;

public class KafkaException extends RuntimeException {

    public KafkaException(String message, Throwable t) {
        super(message, t);
    }

    public KafkaException(String message) {
        super(message, null);
    }

    public KafkaException(Throwable t) {
        super("", t);
    }
}
