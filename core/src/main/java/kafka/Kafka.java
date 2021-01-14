package kafka;

import kafka.server.KafkaServerStartable;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Kafka {

    private Map<String, String> map;
    private String foo = map.get("hello");

    public Kafka(Map<String, String> map) {
        this.map = map;
    }

    public static Properties getPropsFromArgs(String[] args) {
        return null;
    }

    public static void main(String[] args) {
        try {
            Properties serverProps = getPropsFromArgs(args);
            KafkaServerStartable kafkaServerStartable = KafkaServerStartable.fromProps(serverProps);

            // attach shutdown handler to catch control-c
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaServerStartable::shutdown));

            kafkaServerStartable.startup();
            kafkaServerStartable.awaitShutdown();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    public static void main1(String[] args) {
        Map<String, String> m = new HashMap<>();
        m.put("hello", "world");
        System.out.println(new Kafka(m).foo);
    }
}
