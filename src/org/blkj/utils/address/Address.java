/*
 * 
 * Address.java
 */
package org.blkj.utils.address;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.blkj.utils.StringTool;

public class Address {
   


    /**
     * 根据国家统计局颁布的地名解析，http://www.stats.gov.cn/tjbz/xzqhdm/index.htm
     *
     */
    public List<Map<String, String>> address(String addressFile) throws IOException {
        URL url = org.blkj.utils.address.Address.class.getResource(addressFile);
        
        String s = StringTool.getStringFromURL(url);


        String[] ss = StringTool.stringToArray(s, "\r\n");
        if (!s.contains("\r\n")) {
            if (s.contains("\r")) {
                ss = StringTool.stringToArray(s, "\r");
            } else if (s.contains("\n")) {
                ss = StringTool.stringToArray(s, "\n");
            }
        }


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        for (int i = 0; i < ss.length; i++) {
            String[] sss = StringTool.stringToArray(ss[i], "\t");

            if (sss.length > 1) {
                map = new LinkedHashMap<String, String>();
                map.put("addresscode", sss[0].trim());
                map.put("addressname", sss[1].trim());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 根据国家统计局颁布的地名解析，http://www.stats.gov.cn/tjbz/xzqhdm/index.htm
     * tjj_address_2012.txt的内容来自http://www.stats.gov.cn/tjbz/xzqhdm/t20130118_402867249.htm
     */
    public List<Map<String, String>> address() throws IOException {
        return this.address("cn_address_2012.txt");
    }

}
