/*
 * EconomyClassification.java
 * ����
 */
package org.blkj.utils.economyclassification;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.blkj.utils.StringTool;

/**
 * 国民经济行业分类（GB/T 4754—2011）
 * cn_economyclassification_2012.txt来源于国家统计局http://www.stats.gov.cn/tjbz/t20130114_402865889.htm
 *
 * @author hkm
 */
public class EconomyClassification {

    public static void main(String[] args) throws IOException {
        EconomyClassification e = new EconomyClassification();
        List<Map<String, String>> v = e.hangyefenlei();
        for (Map<String, String> m : v) {
            
        }
    }

    /**
     * 解析cn_economyclassification_2012.txt 提取四个要素：三次产业【经济】分类、门类【经济领域】、大类【行业】、名称
     *
     * @return 返回每条记录均包含四个要素的记录集
     */
    public List<Map<String, String>> hangyefenlei() throws IOException {

        URL url = org.blkj.utils.economyclassification.EconomyClassification.class.getResource("cn_economyclassification_2012.txt");
        String s = StringTool.getStringFromURL(url);


        String[] ss = StringTool.stringToArray(s, "\r\n");
        if (!s.contains("\r\n")) {
            if (s.contains("\r")) {
                ss = StringTool.stringToArray(s, "\r");
            } else if (s.contains("\n")) {
                ss = StringTool.stringToArray(s, "\n");
            }
        }

        int begin = 0;
        if (ss.length > 2) {
            begin = 2;
        }


        List<Map<String, String>> _list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        for (int i = begin; i < ss.length; i++) {
            String[] sss = StringTool.stringToArray(ss[i], "\t");
            
            if (sss.length == 4) {
                map = new LinkedHashMap<String, String>();
                sss[0] = sss[0].replace("　", "");//汉字空格
                map.put("economy", sss[0].trim());//三大产业(行业)
                sss[1] = sss[1].replace("　", "");//汉字空格
                map.put("economyfield", sss[1].trim());//门类(领域)
                sss[2] = sss[2].replace("　", "");//汉字空格
                map.put("economycode", sss[2].trim());//大类
                sss[3] = sss[3].replace("　", "");//汉字空格
                map.put("economyname", sss[3].trim());//名称
                _list.add(map);
            }
            if (sss.length == 3) {
                map = new LinkedHashMap<String, String>();
                map.put("economy", "");
                sss[0] = sss[0].replace("　", "");//汉字空格
                map.put("economyfield", sss[0].trim());
                sss[1] = sss[1].replace("　", "");//汉字空格
                map.put("economycode", sss[1].trim());
                sss[2] = sss[2].replace("　", "");//汉字空格
                map.put("economyname", sss[2].trim());
                _list.add(map);
            }
        }






        String economy = "";//产业
        String economyfield = "";//门类

        boolean d1 = false;//第一产业
        boolean d2 = false;//第二产业
        boolean d3 = false;//第三产业


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < _list.size(); i++) {
            Map<String, String> mm = _list.get(i);
            String cy = (String) mm.get("economy");
            if (!"".equals(cy)) {
                economy = cy;
            }
            String ml = (String) mm.get("economyfield");

            if (StringTool.matches("[A-Za-z]", ml)) {
                economyfield = ml;
            }



            if (cy.startsWith("第")) {
                Map<String, String> _m = new LinkedHashMap<String, String>();


                if (cy.startsWith("第一产业")) {
                    if (!d1) {
                        _m.put("economy", "第一产业");
                        _m.put("economyfield", "");
                        _m.put("economycode", "");
                        _m.put("economyname", "农、林、牧、渔业（不含农、林、牧、渔服务业）");

                        list.add(_m);
                        d1 = true;
                    }
                }
                if (cy.startsWith("第二产业")) {
                    if (!d2) {
                        _m.put("economy", "第二产业");
                        _m.put("economyfield", "");
                        _m.put("economycode", "");
                        _m.put("economyname", "采矿业（不含开采辅助活动），制造业（不含金属制品、机械和设备修理业），电力、热力、燃气及水生产和供应业，建筑业");

                        list.add(_m);
                        d2 = true;
                    }
                }
                if (cy.startsWith("第三产业")) {
                    if (!d3) {
                        _m.put("economy", "第三产业");
                        _m.put("economyfield", "");
                        _m.put("economycode", "");
                        _m.put("economyname", "包括：批发和零售业，交通运输、仓储和邮政业，住宿和餐饮业，信息传输、软件和信息技术服务业，金融业，房地产业，租赁和商务服务业，科学研究和技术服务业，水利、环境和公共设施管理业，居民服务、修理和其他服务业，教育，卫生和社会工作，文化、体育和娱乐业，公共管理、社会保障和社会组织，国际组织，以及农、林、牧、渔业中的农、林、牧、渔服务业，采矿业中的开采辅助活动，制造业中的金属制品、机械和设备修理业");

                        list.add(_m);
                        d3 = true;
                    }
                }
            }

            if (economy.startsWith("第三产业")) {
                economy = "第三产业";
            }
            mm.put("economy", economy);
            mm.put("economyfield", economyfield);

            list.add(mm);

        }



        return list;
    }
}
