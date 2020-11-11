package org.blkj.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class MapComparator {

	
	class MapKeyComparator implements Comparator<String>{

	    @Override
	    public int compare(String str1, String str2) {
	        return str1.compareTo(str2);
	    }
	}

	/**
	 * 使用 Map按key进行排序
	 * @param map
	 * @return
	 */
	/*public static Map<String, String> sortMapByKey(Map<String, String> map) {
	    if (map == null || map.isEmpty()) {
	        return null;
	    }

	    Map<String, String> sortMap = new TreeMap<String, String>(
	            new MapKeyComparator());

	    sortMap.putAll(map);

	    return sortMap;
	}
	}*/
	
	
	
	class MapValueComparator implements Comparator<Map.Entry<String, Object>> {
		@Override
		public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
			return ((String) o1.getValue()).compareTo((String) o2.getValue());
		}
	}

	//按值排序(sort by value)
	/**
	 * 使用 Map按value进行排序
	 * @param map
	 * @return

	public Map<String, Object> sortMapByValue(Map<String, Object> oriMap) {
	    if (oriMap == null || oriMap.isEmpty()) {
	        return null;
	    }
	    Map<String,Object> sortedMap = new LinkedHashMap<String, Object>();
	    List<Map.Entry<String, Object>> entryList = new ArrayList<Map.Entry<String, Object>>(
	            oriMap.entrySet());
	    Collections.sort(entryList, new MapValueComparator());

	    Iterator<Map.Entry<String, Object>> iter = entryList.iterator();
	    Map.Entry<String, Object> tmpEntry = null;
	    while (iter.hasNext()) {
	        tmpEntry = iter.next();
	        sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
	    }
	    return sortedMap;
	}
	 */
}
