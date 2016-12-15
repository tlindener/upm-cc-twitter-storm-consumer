/**
* The twitter-hastag-topology is an implementation of Apache Storm to analyze tweets coming from Kafka
* @author  Tobias Lindener
* @version 1.0
* @since   2016-11-22 
*/
package master2016;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

public class CountBolt implements IRichBolt {
	private static final long serialVersionUID = -4010102946325148515L;
	// Map<String, Integer> counters;
	Object2IntLinkedOpenHashMap<String> counters;
	private OutputCollector collector;
	String language;
	String keyword;
	boolean keyWordAppeared = false;
	String folder;
	List<String> lines;

	public CountBolt(String language, String keyword, String folder) {
		this.language = language;
		this.keyword = keyword;
		this.folder = folder;
		this.lines = new ArrayList<String>();
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.counters = new Object2IntLinkedOpenHashMap<String>();
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String str = input.getString(0);

		if (str.equalsIgnoreCase(keyword) && this.keyWordAppeared) {
			// deactivate keyword listener
			this.keyWordAppeared = false;
			if (!counters.isEmpty()) {
				lines.add(saveLine(counters));
				writeLogFile("");
				// System.out.println("Overwrite counters");
				counters = new Object2IntLinkedOpenHashMap<String>();
				// System.out.println("size of counters " + counters.size());
			}

		} else if (str.equalsIgnoreCase(keyword) && !this.keyWordAppeared) {
			// activate keyword listener
			this.keyWordAppeared = true;

		} else if (this.keyWordAppeared) {
			if (!counters.containsKey(str)) {
				counters.put(str, 1);
				// System.out.println(str + "," + 1);
			} else {
				int c = counters.getInt(str) + 1;
				counters.put(str, c);
				// System.out.println(str + "," + c);
			}

		}
		collector.ack(input);

	}

	@Override
	public void cleanup() {

		writeLogFile("-cleanup");

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
	private static List<Entry<String, Integer>> sortByComparator(Map<String, Integer> unsortMap) {

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {

				int value = o2.getValue().compareTo(o1.getValue());
				int key = o1.getKey().compareToIgnoreCase(o2.getKey());
				if(value != 0)
					return value;
				else
					return key;
				

			}
		});

		
		return list;
		}

	private String saveLine(Object2IntLinkedOpenHashMap<String> hashtags) {
		// System.out.println("Save line");
		
		List<Entry<String,Integer>> sorted = sortByComparator(hashtags);
		StringBuffer sb = new StringBuffer();
		sb.append(",");
		sb.append(this.language);
		int cnt = 1;

		for (Entry<String, Integer> i : sorted) {
			if (cnt > 3) {
				break;
			}
			// System.out.println("," + i.getKey() + "," + i.getValue());
			sb.append("," + i.getKey() + "," + i.getValue());
			cnt++;
		}
		return sb.toString();
	}

	private void writeLogFile(String string) {

		FileWriter fw = null;

		try {
			fw = new FileWriter(new File(this.folder, this.language + "_10"+ string +".log").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			for (int i = 0; i < lines.size(); i++) {
				fw.write(i + 1 + lines.get(i) + "\n");
			}
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}
