package master2016;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

public class Test {
	public static boolean ASC = true;
	public static boolean DESC = false;
	
	static List<String> lines = new ArrayList<String>();
	

	public static void main(String[] args) {

		Object2IntLinkedOpenHashMap<String> counters = new Object2IntLinkedOpenHashMap<String>();
		counters.addTo("C", 50);
		counters.addTo("A", 40);
		counters.addTo("BC", 30);
		counters.addTo("BA", 30);
		System.out.println(saveLine(counters));
		
		// Creating dummy unsorted map
		Map<String, Integer> unsortMap = new HashMap<String, Integer>();
		unsortMap.put("A", 55);
		unsortMap.put("BC", 30);
		unsortMap.put("BA", 30);
		lines.add(saveLine(unsortMap));
		writeLogFile("");
	unsortMap.put("C", 70);
	unsortMap.put("D", 80);
		lines.add(saveLine(unsortMap));
		writeLogFile("");


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

	public static void printMap(Map<String, Integer> map) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
	}
	public static void printMap(List<Entry<String, Integer>> map) {
		for (Entry<String, Integer> entry : map) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
	}
	private static void writeLogFile(String string) {

		FileWriter fw = null;

		try {
			fw = new FileWriter(new File("C:\\Users\\tobias\\Desktop\\" +"10"+ string +".log").toString());
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
	private static String saveLine(Map<String, Integer> unsortMap) {
		// System.out.println("Save line");
		
		List<Entry<String,Integer>> sorted = sortByComparator(unsortMap);
		StringBuffer sb = new StringBuffer();
		sb.append(",");
		sb.append("en");
		int cnt = 1;

		for (Entry<String, Integer> i : sorted) {
			if (cnt > 3) {
				break;
			}
			//System.out.println("," + i.getKey() + "," + i.getValue());
			sb.append("," + i.getKey() + "," + i.getValue());
			cnt++;
		}
		return sb.toString();
	}


}
