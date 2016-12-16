package master2016.spouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class CustomKafkaSpout extends BaseRichSpout {

	private SpoutOutputCollector collector;
    private KafkaConsumer<String, String> consumer;
    private String kafkaBrokerUrls;
private Collection<String> topics;

    public CustomKafkaSpout(String kafkaBrokerUrls, String topic) {
        this.kafkaBrokerUrls = kafkaBrokerUrls;
        this.topics = new ArrayList<String>();
        this.topics.add(topic);
        
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        // TODO move hardcoded arguments to the topology
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaBrokerUrls);
        // TODO set static group.id
        properties.put("group.id", ((Long) System.currentTimeMillis()).toString());
        // TODO true or false?
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // This could not be thread safe
        consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(topics);
        this.collector = collector;
    }

    public void nextTuple() {
        ConsumerRecords<String, String> records = consumer.poll(0);

        if (!records.isEmpty()) {
            
            for (ConsumerRecord<String, String> record : records) {
                String hashtag = record.value();
                // non blocking operation
                collector.emit(new Values(hashtag));

            
            }
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("hashtag"));
    }
}
