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
	
	private static final long serialVersionUID = 5333992976679079370L;
	private SpoutOutputCollector collector;
	private KafkaConsumer<String, String> consumer;
	private String kafkaBrokerUrls;
	private Collection<String> topics;
	private String topic;
	public CustomKafkaSpout(String kafkaBrokerUrls, String topic) {
		this.kafkaBrokerUrls = kafkaBrokerUrls;
		this.topics = new ArrayList<String>();
		this.topics.add(topic);
		this.topic = topic;
	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaBrokerUrls);
		properties.put("group.id", topic);
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// This could not be thread safe
		consumer = new KafkaConsumer<>(properties);

		//subscribe to language topic
		consumer.subscribe(topics);
		this.collector = collector;
	}

	public void nextTuple() {
		ConsumerRecords<String, String> records = consumer.poll(0);

		if (!records.isEmpty()) {

			for (ConsumerRecord<String, String> record : records) {
				//receive hashtag from Kafka
				String hashtag = record.value();
				collector.emit(new Values(hashtag));

			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("hashtag"));
	}
}
