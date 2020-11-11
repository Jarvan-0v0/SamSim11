/*
 * People.java
 * ����
 */
package org.blkj.utils.people;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.blkj.utils.StringTool;

/**
 * 中国的民族
 *
 * @author hkm
 */
public class People {



    public List<Map<String, String>> peopleList() throws IOException {
        return peopleList("cn_people_2012.txt");
    }

    /**
     * 来自http://www.gov.cn/test/2006-04/04/content_244533.htm的内容，经过在wps表格中整理的结果cn_people_2012.txt
     * 解析cn_people_2012.txt
     *
     */
    public List<Map<String, String>> peopleList(String peopleFile) throws IOException {
        URL url = org.blkj.utils.people.People.class.getResource(peopleFile);
        
        String s = StringTool.getStringFromURL(url);
        String[] ss = StringTool.stringToArray(s, "\r\n");
        if (s.contains("\r\n")) {
            ss = StringTool.stringToArray(s, "\r\n");
        } else {
            if (s.contains("\n")) {
                ss = StringTool.stringToArray(s, "\n");
            } else if (s.contains("\n")) {
                ss = StringTool.stringToArray(s, "\n");
            }
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        for (int i = 0; i < ss.length; i++) {
            if (i > 1) {//从第三行"汉族"开始
                String[] ss1 = StringTool.stringToArray(ss[i], "\t");
                if (ss1.length == 3) {
                    map = new LinkedHashMap<String, String>();
                    map.put("minzu", ss1[0].trim());
                    map.put("pinxie", ss1[1].trim());
                    map.put("daima", ss1[2].trim());
                    list.add(map);
                }
            }
        }
        return list;
    }
}
