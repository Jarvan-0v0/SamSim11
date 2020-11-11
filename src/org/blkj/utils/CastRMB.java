package org.blkj.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.blkj.utils.base.RegexType;

/**
 * 将标准的小写人民币转换成大写人民币，支持万亿级大写的精准转换
 *
 * @author hkm
 */
public class CastRMB {

    /**
     * 将小写人民币转换成大写人民币
     *
     * @param jine 原始的小写
     * @return 返回大写人民币
     */
    public String cast(BigDecimal jine) {
        return cast(jine.toPlainString());
    }

    /**
     * 将小写人民币转换成大写人民币
     *
     * @param jine 原始的小写
     * @return 返回大写人民币
     */
    public String cast(String jine) {

        String rmb = "";

        String[] dwzs = {"万", "仟", "佰", "拾", "亿", "仟", "佰", "拾", "万", "仟", "佰", "拾", ""};//支持万亿元的转换，长度13，整数部分单位
        String[] shu = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"}; // 数字表示
        String s1 = "";
        String s2 = "";
        if (jine.contains(".")) {
            s1 = jine.substring(0, jine.indexOf("."));
            s2 = jine.substring(jine.indexOf(".") + 1);
        } else {
            s1 = jine;
        }

        String jineStr = convertFirst(s1);//初步转换

        if (jineStr.length() <= dwzs.length) {
            int DWZS_POS = dwzs.length - jineStr.length();//定位dwzs[]的位置
            char[] c1 = jineStr.toCharArray();
            int len = jineStr.length();//长度
            int last = jineStr.length() - 1;//最后一位
            if (len > 4) {
                //万元部分 
                if (c1[last - 4] == '零') {
                    c1[last - 4] = '0';
                }
                if (len > 8) {
                    //亿元部分 
                    if (c1[last - 8] == '零') {
                        c1[last - 8] = '0';
                    }
                }
            }
            //转换
            for (int i = 0; i < len; i++) {
                if (c1[i] != '0') {
                    if (c1[i] == '零') {
                        rmb = rmb + c1[i];
                    } else {
                        int n = Integer.parseInt("" + c1[i]);
                        rmb = rmb + shu[n] + dwzs[DWZS_POS + i];
                    }
                } else {
                    if ((DWZS_POS + i) == 4) {
                        rmb = rmb + dwzs[DWZS_POS + i];//亿的处理
                    }
                    if ((DWZS_POS + i) == 8) {
                        //万的处理
                        if (len > 8) {
                            if (c1[last - 5] != '0' || c1[last - 6] != '0' || c1[last - 7] != '0') {
                                rmb = rmb + dwzs[DWZS_POS + i];
                            }
                        } else {
                            rmb = rmb + dwzs[DWZS_POS + i];
                        }
                    }
                }
            }
            if (!"".equals(rmb)) {
                rmb = rmb + "元";
            }
            if ("".equals(s2)) {
                rmb = rmb + "整";
            } else if ("0".equals(s2) || "00".equals(s2) || "000".equals(s2)) {
                rmb = rmb + "整";
            } else if (s2.length() == 1) {
                int jue = Integer.parseInt("" + s2.charAt(0));
                rmb = rmb + shu[jue] + "角" + 0 + "分";
            } else if (s2.length() == 2) {
                int jue = Integer.parseInt("" + s2.charAt(0));
                int fen = Integer.parseInt("" + s2.charAt(1));
                rmb = rmb + shu[jue] + "角" + shu[fen] + "分";
            } else if (s2.length() >= 3) {
                int jue = Integer.parseInt("" + s2.charAt(0));
                int fen = Integer.parseInt("" + s2.charAt(1));
                int li = Integer.parseInt("" + s2.charAt(2));
                rmb = rmb + shu[jue] + "角" + shu[fen] + "分" + shu[li] + "厘";
            }
            if ("0".equals(jine) || "0.0".equals(jine) || "0.00".equals(jine) || "0.000".equals(jine)) {
                rmb = "零元整";
            }
        } else {
            throw new java.lang.ArrayIndexOutOfBoundsException("整数部分的长度是" + s1.length() + "，已经超过了最大长度限制，请将整数部分的数据控制在" + dwzs.length + "位数之内。");
        }
        return rmb;

    }

    /**
     * 初次转换金额，按照开头非0结尾0的规则分解字符串。
     *
     * @param jine 金额
     * @return 返回初次转换值
     */
    private String convertFirst(String jine) {
        String jineStr = "";
        boolean b = StringTool.matches(RegexType.number, jine);//判断格式是否正确
        if (b) {
            List<Integer> v = new ArrayList<Integer>();
            int fromIndex = 0;
            indexPos(v, fromIndex, jine);
            int lastIndex = (Integer) v.get(v.size() - 1);
            String lastStr = jine.substring(lastIndex);
            for (int i = 0; i < v.size() - 1; i++) {
                int index = (Integer) v.get(i);
                int next = (Integer) v.get(i + 1);
                String s = jine.substring(index, next);
                s = s.substring(0, s.length() - 1) + "零";//将每个子串尾部0变成"零"
                jineStr = jineStr + s;
            }
            jineStr = jineStr + lastStr;
            if ("".equals(lastStr)) {
                if (jineStr.endsWith("零")) {
                    jineStr = jineStr.substring(0, jineStr.length() - 1) + "0";//最后的0不需要转换
                }
            }
        }
        return jineStr;
    }

    /**
     * 递归查找下一个非0的起始索引位置
     *
     * @param v 存储索引的非0的起始位置的指针数组
     * @param fromIndex 当前起始位
     * @param jine 原始的阿拉伯小写人民币字符串
     */
    private void indexPos(List<Integer> v, int fromIndex, String jine) {
        int index = jine.indexOf("0", fromIndex);
        if (fromIndex != index) {
            v.add(fromIndex);
        }
        if (index >= 0) {
            fromIndex = index + 1;
            indexPos(v, fromIndex, jine);
        }
    }

}
