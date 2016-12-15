package master2016;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import master2016.bolts.CountBolt;

public class ComparatorTest {

	Object2IntLinkedOpenHashMap<String> testData;
	
	@Before
	public void setUp() throws Exception {
		testData  = new Object2IntLinkedOpenHashMap<String>();
		testData.addTo("B", 100);
		testData.addTo("A", 80);
		testData.addTo("CD", 100);
		testData.addTo("CC", 100);
		testData.addTo("H", 120);
	}

	@Test
	public void test() {
		List<Entry<String,Integer>>result = CountBolt.sortByComparator(testData);
		Assert.assertSame(120, result.get(0).getValue());
		Assert.assertSame(100, result.get(1).getValue());
		Assert.assertSame(100, result.get(2).getValue());
		Assert.assertSame(100, result.get(3).getValue());
		Assert.assertSame(80, result.get(4).getValue());
		
		Assert.assertSame("H", result.get(0).getKey());
		Assert.assertSame("B", result.get(1).getKey());
		Assert.assertSame("CC", result.get(2).getKey());
		Assert.assertSame("CD", result.get(3).getKey());
		Assert.assertSame("A", result.get(4).getKey());
	}

}
