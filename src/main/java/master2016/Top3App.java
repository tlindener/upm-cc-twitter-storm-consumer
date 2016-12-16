/**
* The twitter-hastag-topology is an implementation of Apache Storm to analyze tweets coming from Kafka
* @author  Tobias Lindener
* @version 1.0
* @since   2016-11-22 
*/
package master2016;

import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import master2016.bolts.CountBolt;
import master2016.spouts.CustomKafkaSpout;

public class Top3App {
	public static void main(String[] args) throws Exception {

		System.out.println("Please use the kafkaUrl!");
		if(args.length < 4 )
		{
			System.out.println("Parameters missing. Use following definition");
			System.out.println("languageList kafkaUrl stormTopologyName folderName");
		}
		Config config = new Config();
		// Used in case of debugging
		// config.setDebug(true);
		config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);

		HashMap<String, String> langList = new HashMap<String, String>();
		TopologyBuilder builder = new TopologyBuilder();

		// split language and keyword list
		String[] pairs = args[0].split(",");
		for (String pair : pairs) {
			System.out.println("Language Pair: " + pair);
			String[] langPair = pair.split(":");
			langList.put(langPair[0], langPair[1]);
		}

		String zookeeperUrl = args[1];
		String topologyName = args[2];
		String folder = args[3];
	
		//dynamically build Topology, using one Spout for each language topic on Kafka
		for (Entry<String, String> lang : langList.entrySet()) {
			//Setup Spout with zookeeper Url
//			SpoutConfig kafkaSpoutConfig = new SpoutConfig(new ZkHosts(zookeeperUrl), lang.getKey(),
//					"/" +  lang.getKey(), UUID.randomUUID().toString());
//			kafkaSpoutConfig.bufferSizeBytes = 4096 * 4096 * 4;
//			kafkaSpoutConfig.fetchSizeBytes = 4096 * 4096 * 4;		
//			kafkaSpoutConfig.scheme = new org.apache.storm.spout.SchemeAsMultiScheme(new StringScheme());
			builder.setSpout(lang + " kafka-spout", new CustomKafkaSpout(zookeeperUrl,lang.getKey()));
			//Set CountBolt with language and key word parameter
			builder.setBolt(lang + " word-counter", new CountBolt(lang.getKey(), lang.getValue(), folder))
					.shuffleGrouping(lang + " kafka-spout");
		}


		StormSubmitter.submitTopology(topologyName, config, builder.createTopology());
		
		// Implementation based on the local cluster
		
		//LocalCluster cluster = new LocalCluster();
		//cluster.submitTopology(topologyName, config, builder.createTopology());
		//Thread.sleep(10000);
		//cluster.shutdown();

	}
}