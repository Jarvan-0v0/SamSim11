/*
 * AccountingSubject.java
 * ����
 * 2013-01-23
 */
package org.blkj.utils.accountingsubject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.blkj.utils.StringTool;
/**
 * 中国会计科目
 *
 * @author hkm
 */
public class AccountingSubject {


    public List<Map<String, String>> kemuList() throws IOException {
        return kemuList("accountingsubject_2011.txt");
    }

    /**
     * 来自http://www.gov.cn/test/2006-04/04/content_244533.htm的内容，经过在wps表格中整理的结果cn_kemu_2012.txt
     * 解析accountingsubject_2011.txt
     *
     */
    public List<Map<String, String>> kemuList(String kemuFile) throws IOException {
        URL url = org.blkj.utils.accountingsubject.AccountingSubject.class.getResource(kemuFile);
        
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
            String[] ss1 = StringTool.stringToArray(ss[i], "\t");
            if (ss1.length >= 2) {
                map = new LinkedHashMap<String, String>();
                map.put("code", ss1[0].trim());
                map.put("kemu", ss1[1].trim());
                if (ss1.length > 2) {
                    map.put("memo", ss1[2].trim());
                }

                list.add(map);
            }

        }
        return list;
    }
}