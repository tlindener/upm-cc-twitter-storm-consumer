/**
* The twitter-hastag-topology is an implementation of Apache Storm to analyze tweets coming from Kafka
* @author  Tobias Lindener
* @version 1.0
* @since   2016-11-22 
*/
package master2016.bolts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

public class CountBolt extends BaseRichBolt {
	private static final long serialVersionUID = -4010102946325148515L;
	// Map<String, Integer> counters;
	Object2IntLinkedOpenHashMap<String> counters;
	private OutputCollector collector;
	String language;
	String keyword;
	boolean keyWordAppeared = false;
	String folder;
	int lineCounter = 1;

	public CountBolt(String language, String keyword, String folder) {
		this.language = language;
		this.keyword = keyword;
		this.folder = folder;
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.counters = new Object2IntLinkedOpenHashMap<String>();
		this.collector = collector;
	}

	public void execute(Tuple input) {
		String str = input.getString(0);

		if (str.equalsIgnoreCase(keyword) && this.keyWordAppeared) {
			// deactivate keyword listener
			this.keyWordAppeared = false;
			if (!counters.isEmpty()) {
				writeAppendLine(saveLine(counters,lineCounter));
				lineCounter++;
				counters = new Object2IntLinkedOpenHashMap<String>();

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

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
	public static List<Entry<String, Integer>> sortByComparator(Map<String, Integer> unsortMap) {

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {

				// compare based on the count of the hashtag
				int value = o2.getValue().compareTo(o1.getValue());
				//additionally compare based on the alphabetical order
				int key = o1.getKey().compareTo(o2.getKey());
				
				// if the values are equal, compare based on the hashtag option
				if(value != 0)
					return value;
				else
					return key;			

			}
		});

		
		return list;
		}

	private String saveLine(Object2IntLinkedOpenHashMap<String> hashtags,int counter) {
		// System.out.println("Save line");
		
		List<Entry<String,Integer>> sorted = sortByComparator(hashtags);

		//use string buffer to optimize string operations
		StringBuffer sb = new StringBuffer();
		sb.append(counter);
		sb.append(",");
		sb.append(this.language);
		int cnt = 1;

		for (Entry<String, Integer> i : sorted) {
			if (cnt > 3) {
				break;
			}

			sb.append(",");
			sb.append(i.getKey());
			sb.append(",");
			sb.append(i.getValue());
			cnt++;
		}
		if(sorted.size() == 1){
			sb.append(",null,0,null,0");
		}
		if(sorted.size() == 2){
			sb.append(",null,0");
		}
		//ensure line break
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}

	private void writeAppendLine(String line){
		 
		PrintWriter fw = null;

		try {
			//Securely create a path out of multiple components
			String filePath = new File(this.folder, this.language + "_10.log").toString();
			//use fileWriter in order to ensure append instead of overwrite
			fw = new PrintWriter(new FileWriter(filePath, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {			
			fw.append(line);
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (fw != null) {
				fw.close();
			}

		}

	}
	

}

