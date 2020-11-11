/*
 * LunarYear.java
 * ����
 * 2012-10-28
 * 
 * ��ݸ�Ĺ����꣬��������ũ���������ũ���¡��յ������Ϣ��
 */
package org.blkj.utils.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author hkm
 */
public class LunarYear {


	    /**
	     * 公元年
	     */
	    private int year = 1900;
	    //private final String baseDate = "1900-01=31";
	    private String ganzhiName = "庚子";//干支年名
	    private String animalName = "鼠";//生肖
	    private int days = 348;//农历年的天数
	    /**
	     * 是否在[1900-01-31,2050-01-30]之间
	     */
	    private boolean isIn;
	    /**
	     * 这一年农历年所有的农历月信息
	     */
	    List<LunarMonth> months=new ArrayList<LunarMonth>();
	    /**
	     * 农历年信息
	     */
	    long lunarInfo;//如： 0x04bd8;////注：这个数组代表了1900-1-31(庚子年春节)庚子年的农历信息
	    /**
	     * 农历年第一天对应的公历年的某一天
	     */
	    private java.util.Date beginDate;
	    public static final int[] lunarYearDays=new int[Lunar.lunarInfo.length];
	    
	    static{
	        for(int i=0;i<lunarYearDays.length;i++){
	            lunarYearDays[i]=_countDaysOneLunarYear(Lunar.lunarInfo[i]);
	        }
	    }
	    
	    // 返回农历y年的天数
	    private static int _countDaysOneLunarYear(long lunarInfo) {
	        List<Integer> v = new ArrayList<Integer>();
	        long x = lunarInfo;
	        x = x % 0x10000;//去头
	        x = x >>> 4;//斩尾
	        String monthInfo = Long.toString(x, 2);//取中间，提取[4-15]位二进制信息，
	        for (int i = 0; i < monthInfo.length(); i++) {
	            int m = 29;
	            char c = monthInfo.charAt(i);
	            if (c == '1') {
	                m=30;
	            }
	            v.add(m);
	        }
	        //添加闰月的天数
	        int runyue = (int) (lunarInfo % 16);//取闰月的月份
	        if (runyue > 0) {
	            long _info = lunarInfo;
	            int daxiao = (int) (_info >>> 16);//获取闰月的大小
	            int m = 29;
	            if (daxiao == 1) {
	                m=30;
	            }
	            v.add(runyue, m);//插入闰月的天数
	        }
	        //统计农历年的天数
	        int num = 0;
	        for (int i=0;i<v.size();i++) {
	            num = num + (Integer)v.get(i);
	        }
	        return num;
	    }
	    
	    public LunarYear(){
	        //查询当天的农历日期
	    }
	    
	    /**
	     * 根据公历日期查询农历信息
	     */
	    public LunarYear(int year,int month,int day){
	        //指定日期查询农历日期
	    }
	    
	    /**
	     * 某一年的农历
	     */
	    public LunarYear(int year) {
	        this.year = year;
	        int y = year;                               //n是公元年，如-5000,-56,998,2012
	        y = (y - 4) % 60;                           //参照公元后的第一个甲子年为计算条件
	        if (y >= 0) {
	            ganzhiName = Lunar.ganzhi[y];           //公元后的干支年
	        } else {
	            ganzhiName = Lunar.ganzhi[60 + y];      //公元前的干支年
	        }
	        
	        y = year;                                   //n是公元年，如-5000,-56,998,2012
	        y = (y - 4) % 12;                           //参照公元后的第一个生肖年(同甲子年)为计算条件
	        if (y >= 0) {
	            animalName = Lunar.animal[y];           //公元后的生肖年
	        } else {
	            animalName = Lunar.animal[12 + y];      //公元前的生肖年
	        }
	        
	        if (year >= 1900 && year < 2050) {
	            isIn = true;
	            initMonth(Lunar.lunarInfo[year - Lunar.REF_YEAR]);//初始化每个农历月信息，统计农历年的天数
	        } 
	    }
	    
	    

	    // 初始化农历月
	    private void initMonth(long lunarInfo) {
	        //判断是否在1900-1-31与2050-1-30之间
	        this.months = new ArrayList<LunarMonth>();
	        //添加月信息
	        long x = lunarInfo;
	        x = x % 0x10000;
	        x = x >>> 4;
	        String monthInfo = Long.toString(x, 2);//提取[4-15]位二进制信息，输出格式:101010010111
	        for (int i = 0; i < monthInfo.length(); i++) {
	            LunarMonth m = new LunarMonth();
	            m.setId(i);
	            m.setMonthName(Lunar.monthName[i]);
	            char c = monthInfo.charAt(i);
	            if (c == '1') {
	                m.setDaysize(30);
	            }
	            if (c == '0') {
	                m.setDaysize(29);
	            }
	            months.add(m);
	        }

	        //添加闰月信息
	        int runyue = (int) (lunarInfo % 16);//取闰月的月份
	        if (runyue > 0) {
	            long _info = lunarInfo;
	            int daxiao = (int) (_info >>> 16);//获取闰月的大小
	            LunarMonth m = new LunarMonth();
	            m.setMonthName("闰" + Lunar.monthName[runyue - 1]);
	            m.setIsLeapMonth(true);
	            if (daxiao == 1) {
	                m.setDaysize(30);
	            } else {
	                m.setDaysize(29);
	            }
	            months.add(runyue, m);//插入闰月
	            int mm_id = 0;
	            for (LunarMonth mm : months) {
	                mm.setId(mm_id);//插入闰月后，重新设置农历月的序号
	                mm_id++;
	            }
	        }

	        //统计农历年的天数
	        int num = 0;
	        for (LunarMonth m : months) {
	            num = num + m.getDaysize();
	        }
	        this.days = num;
	    }

	    public int getYear() {
	        return year;
	    }


	    public String getGanzhiName() {
	        return ganzhiName;
	    }


	    public String getAnimalName() {
	        return animalName;
	    }


	    public int getDays() {
	        return days;
	    }


	    public boolean isIsIn() {
	        return isIn;
	    }


	    public List<LunarMonth> getMonths() {
	        return months;
	    }


	    public long getLunarInfo() {
	        return lunarInfo;
	    }

	    public Date getBeginDate() {
	        return beginDate;
	    }


	}
