/*
 * 实用日期操作
 * 胡开明
 * 2008年12月
 * 目前主要针对中国的日期设计，未来做国际化支持
 */
package org.blkj.utils;

import java.io.Serializable;
import java.text.*;
import java.util.*;

import org.blkj.utils.base.Lunar;

import blkjweb.utils.Console;


public class DateTool implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 * 获取YYYY格式
	 * 
	 * @return
	 */
	public static String getYear() {
		return sdfYear.format(new Date());
	}

	public static String getYear(Date date) {
		if (date == null)
			date = new Date();
		return sdfYear.format(date);
	}
	
	
	
void xbm(){}	
	
	
	
	
	
	
	
	
	
	
	
	private Locale locale = Locale.CHINESE;//语言
	private String fulltimeinfo = "";
	private int formatGroup = 0;
	    
	private java.util.Date date;
	private String[] _dateInfo;
	private List<Map<String, Serializable>> formatArray = new ArrayList<Map<String, Serializable>>();
	private Calendar calendar;//日历
	 

    private String datetimeFormat = "yyyy-MM-dd HH:mm:ss";//日期时间格式
    private String dateFormat = "yyyy-MM-dd";//日期格式
    private String timeFormat = "HH:mm:ss";//时间格式
    
	private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public DateTool() {
        date = new java.util.Date();
        _init(date);
    }

    /**
     * @param this_datetime 精确查询的日期时间，如：由2012-9-19
     * 13:23:10产生的datetime值1346566871000
     */
    public DateTool(long this_datetime) {
        date = new java.util.Date(this_datetime);
        _init(date);
    }

    /**
     * 按照默认的日期时间格式解析
     *
     * @param datetime 指定格式的日期，如："2012-10-31 00:00:00"
     */
    public DateTool(String datetime) {
        try {
            DateFormat f = new SimpleDateFormat();
            date = (Date) f.parse(datetime);
            _init(date);
        } catch (ParseException ex) {
            Console.showMessage(DateTool.class.getSimpleName(), ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 根据指定的年月日查询日期信息
     *
     * @param year 指定年
     * @param month 指定月份，取值：[1,12]
     * @param day 指定日
     */
    public DateTool(int year, int month, int day) {
        GregorianCalendar g = new GregorianCalendar(year, month - 1, day);
        this.date = g.getTime();
        _init(date);
    }
    
    private void _init(Date date) {
        _dateInfo = _dateInfo(date);
        Map<String, Serializable> m0 = new HashMap<String, Serializable>();
        m0.put("locale", Locale.SIMPLIFIED_CHINESE);
        m0.put("datetimeFormat", this.datetimeFormat);
        m0.put("dateFormat",this.dateFormat);
        m0.put("timeFormat",this.timeFormat);
        formatArray.add(m0);

        int y = Integer.parseInt(this.year());
        int m = Integer.parseInt(this.month());
        int d = Integer.parseInt(this.day());
        calendar = new GregorianCalendar(y, m - 1, d);
    }
    
    public Date getDate() {
        return date;
    }

    
    /**
     * 实用方法：获取相对于当天偏移量的日期 返回完全格式的日期，如：2012-11-20 08:00:00
     *
     */
    public String today(int offsetToday) {
        java.util.Date date = new java.util.Date();
        long d = date.getTime();
        long off = (long) offsetToday;
        long offset = 1000l * 60l * 60l * 24l * off;
        date = new java.util.Date(d + offset);
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        String s = df.format(date);
        return s;
    }

    /**
     * 实用方法：获取相对于当天偏移量的日期
     *
     */
    public String todayLong(int offsetToday) {
        java.util.Date date = new java.util.Date();
        long d = date.getTime();
        long off = (long) offsetToday;
        long offset = 1000l * 60l * 60l * 24l * off;
        date = new java.util.Date(d + offset);
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        String s = df.format(date) + " " + new java.sql.Time(0);
        return s;
    }

 
    /**
     * 实用方法：查询时间的long值
     *
     * @param datetime : 2011-06-01 00:00:00.0
     *
     */
    public long getTime(String datetime) {
        long l = -1l;
        try {
            java.text.DateFormat fm = new java.text.SimpleDateFormat(datetimeFormat);
            Date date = (java.util.Date) fm.parse(datetime);
            l = date.getTime();
        } catch (ParseException ex) {
            Console.showMessage(DateTool.class.getSimpleName(), ex.getLocalizedMessage(), ex);
        }
        return l;
    }

    /**
     * 实用方法：查询时间的long值
     *
     * @param datetime : 2011-06-01 00:00:00.0
     * @param datetimeFormat 
     * @return 
     *
     */
    public long getTime(String datetime, String datetimeFormat) {
        long l = -1l;
        try {
            java.text.DateFormat fm = new java.text.SimpleDateFormat(datetimeFormat);
            Date date = (java.util.Date) fm.parse(datetime);
            l = date.getTime();
        } catch (ParseException ex) {
            Console.showMessage(DateTool.class.getSimpleName(), ex.getLocalizedMessage(), ex);
        }
        return l;
    }
    /**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String getTime() {
		return sdfTime.format(new Date());
	}
	
	/**
	 * 获取 HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String getShortTime() {
		sdfDay.applyPattern("HH:mm:ss");
		return sdfDay.format(new Date());
	}
	/*
	public String time() {
        DateFormat df = new SimpleDateFormat(timeFormat, locale);
        return df.format(date);
    }*/	
	
	 /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的日期时间
     */
    public String dateTime(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat(datetimeFormat, locale);
        return df.format(new Date(t));
    }

    /**
     * 实用方法：获取当天日期
     *
     */
    public String today() {
        String s = "";
        java.util.Date date = new java.util.Date();
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        s = df.format(date);
        return s;
    }

    /**
     * 实用方法：获取当天日期 返回完全格式的日期，如：2012-11-20 08:00:00
     *
     */
    public String todayLong() {
        String s = "";
        java.util.Date date = new java.util.Date();
        DateFormat df = new SimpleDateFormat(this.dateFormat);
        s = df.format(date) + " " + new java.sql.Time(0);
        return s;
    }
    
     /**
     * 实用方法：获取当时日期时间
     *
     * @param format 如:yyyy-MM-dd HH:mm:ss | yyyy-MM-dd HH | HH:mm:ss
     * |yyyy年MM月dd日 ...
     *
     */
    public String dateTime(String format) {
        String s = "";
        this.datetimeFormat=format;
        java.text.Format fm = new java.text.SimpleDateFormat(format);
        java.util.Date date = new java.util.Date();
        s = fm.format(date);
        return s;
    }

    /**
     * 实用方法：获取当时日期时间，经过实践检验的实用方法
     *
     * @param format 如:yyyy-MM-dd HH:mm:ss | yyyy-MM-dd HH | HH:mm:ss
     * |yyyy年MM月dd日 ...
     *
     */
    public String dateTime(Date date,String format) {
        String s = "";
        java.text.Format fm = new java.text.SimpleDateFormat(format);
        this.date=date;
        this.datetimeFormat=format;
        s = fm.format(date);
        return s;
    }

    /**
     * 返回完全格式的日期，如：2012-11-20 08:00:00
     *
     */
    public String dateLong() {
        DateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(date) + " " + new java.sql.Time(0);
    }

    /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的日期
     */
    public String dateLong(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(new Date(t)) + " " + new java.sql.Time(0);
    }

    /**
     * 返回日期，如：2012-11-20
     *
     */
    public String date() {
        DateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(date);
    }

    /**
     * 返回日期，如：2012-11-20
     *
     */
    public String date(String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(date);
    }

    /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的日期
     */
    public String date(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat(dateFormat, locale);
        return df.format(new Date(t));
    }

    public String time() {
        DateFormat df = new SimpleDateFormat(timeFormat, locale);
        return df.format(date);
    }
    
    public String time(String timeFormat) {
        DateFormat df = new SimpleDateFormat(timeFormat, locale);
        return df.format(date);
    }


    /**
     * 返回中国时区:'08:00:00'
     */
    public String timeZero() {
        return "" + new java.sql.Time(0);
    }

    public String year() {
        return _dateInfo[0];
    }

    /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的年
     */
    public String year(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat("yyyy", locale);
        return df.format(new Date(t));
    }

    public String month() {
        return _dateInfo[1];
    }

    /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的月
     */
    public String month(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat("MM", locale);
        return df.format(new Date(t));
    }

    public String day() {
        return _dateInfo[2];
    }

    /**
     * @param offsetDays 偏差天数
     * @return 返回相对构造函数中指定日期偏差天数的天
     */
    public String day(int offsetDays) {
        long offset = (long)offsetDays * 24l * 60l * 60l * 1000l;
        long t = date.getTime() + offset;
        DateFormat df = new SimpleDateFormat("dd", locale);
        return df.format(new Date(t));
    }

    /**
     * 指定日期中当月的天数
     */
    public int daysInMonth() {
        int days = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }
	
    /**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr,String endDateStr){
        long day=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        
            try {
				beginDate = format.parse(beginDateStr);
				endDate= format.parse(endDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
            //MyLogger.showMessage("相隔的天数="+day);
      
        return day;
    }

    //得到n个月之前的日期
    public static String getBeforeMonthsDate(int months) {
    	int month = 0 - months;
    	Date dNow = new Date();   //当前时间
    	Date dBefore = new Date();
    	Calendar calendar = Calendar.getInstance(); //得到日历
    	calendar.setTime(dNow);//把当前时间赋给日历
    	calendar.add(Calendar.MONTH, month);  //设置为前month月
    	dBefore = calendar.getTime();   //得到前month月的时间

    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
    	String defaultStartDate = sdf.format(dBefore);    //格式化前month月的时间

    	return defaultStartDate;
    }
    
    /**
     * 得到n天之后的日期
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);
        
        return dateStr;
    }
    
    /**
     * 得到n天之后是周几
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
    	int daysInt = Integer.parseInt(days);
    	
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
        
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);
        
        return dateStr;
    }
    /**
     * 返回农历干支年名
     */
    public String lunarYearName() {
        String ganzhi = "";
        int y = Integer.parseInt(year());
        y = (y - 4) % 60;                       //参照公元后的第一个甲子年为计算条件
        if (y >= 0) {
            ganzhi = Lunar.ganzhi[y];           //公元后的干支年
        } else {
            ganzhi = Lunar.ganzhi[60 + y];      //公元前的干支年
        }
        return ganzhi;
    }

    /**
     * 返回生肖（zodiac）年名
     */
    public String animalYearName() {
        String animal = "";
        int y = Integer.parseInt(year());
        y = (y - 4) % 12;                       //参照公元后的第一个生肖年(同甲子年)为计算条件
        if (y >= 0) {
            animal = Lunar.animal[y];           //公元后的生肖年
        } else {
            animal = Lunar.animal[12 + y];      //公元前的生肖年
        }
        return animal;
    }

    /**
     * 实用方法：获取当时日期时间<br/>
     */
    private String[] _dateInfo(Date date) {
        String[] s = new String[12];
        java.text.Format fm = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS.F.E.Z", locale);//"yyyy.MM.dd.HH.mm.ss.SSS.u.E.X"在JavaFX中u和X解析错误
        String _s = fm.format(date);
        int i = 0;
        StringTokenizer st = new StringTokenizer(_s, ".");//这是安全的解析方法；使用s.split(".")解析，得不到需要的结果，是不安全的。
        while (st.hasMoreTokens()) {
            String d = (String) st.nextElement();
            s[i] = d;
            i++;
        }
        fulltimeinfo = _s;
        return s;
    }

    /**
	 * 把时间根据时、分、秒转换为时间段
	 * @param StrDate
	 */
	public static String getTimes(String StrDate){
		String resultTimes = "";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    java.util.Date now;
	    
	    try {
	    	now = new Date();
	    	java.util.Date date=df.parse(StrDate);
	    	long times = now.getTime()-date.getTime();
	    	long day  =  times/(24*60*60*1000);
	    	long hour = (times/(60*60*1000)-day*24);
	    	long min  = ((times/(60*1000))-day*24*60-hour*60);
	    	long sec  = (times/1000-day*24*60*60-hour*60*60-min*60);
	        
	    	StringBuffer sb = new StringBuffer();
	    	//sb.append("发表于：");
	    	if(hour>0 ){
	    		sb.append(hour+"小时前");
	    	} else if(min>0){
	    		sb.append(min+"分钟前");
	    	} else{
	    		sb.append(sec+"秒前");
	    	}
	    		
	    	resultTimes = sb.toString();
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    
	    return resultTimes;
	}


	
	
	
	/**
	 * 获取YYYY-MM-DD格式
	 * 
	 * @return
	 */
	public static String getDay() {
		return sdfDay.format(new Date());
	}
	
	/**
	 * 获取YYYYMMDD格式
	 * 
	 * @return
	 */
	public static String getDays(){
		return sdfDays.format(new Date());
	}

	

	/**
	* @Title: compareDate
	* @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	* @param s
	* @param e
	* @return boolean  
	* @throws
	* @author luguosui
	 */
	public static boolean compareDate(String s, String e) {
		if(fomatDate(s)==null||fomatDate(e)==null){
			return false;
		}
		return fomatDate(s).getTime() >=fomatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static Date fomatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验日期是否合法
	 * 
	 * @return
	 */
	public static boolean isValidDate(String s) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date){
		return date2Str(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date){
		if(StringTool.notEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		}else{
			return null;
		}
	}
	
	/**
	 * 按照参数format的格式，日期转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date,String format){
		if(date!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}else{
			return "";
		}
	}
	
	
	public static int getDiffYear(String startTime,String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			int years=(int) (((fmt.parse(endTime).getTime()-fmt.parse(startTime).getTime())/ (1000 * 60 * 60 * 24))/365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}
	public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setDatetimeFormat(String datetimeFormat) {
        this.datetimeFormat = datetimeFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getFulltimeinfo() {
        return fulltimeinfo;
    }

    public int getFormatGroup() {
        return formatGroup;
    }

    public void setFormatGroup(int formatGroup) {
        this.formatGroup = formatGroup;
    }

    public String dateTime() {
        DateFormat df = new SimpleDateFormat(datetimeFormat, locale);
        return df.format(date);
    }

     /**
     * 指定日期中的日历
     */
    public Calendar getCalendar() {
        return this.calendar;
    }

    public String hour() {
        return _dateInfo[3];
    }

    public String minute() {
        return _dateInfo[4];
    }

    public String secont() {
        return _dateInfo[5];
    }

    public String millisecond() {
        return _dateInfo[6];
    }

    public String numInWeek() {
        return _dateInfo[7];
    }

    public String nameInWeek() {
        return _dateInfo[8];
    }

    public String timeDifference() {
        return _dateInfo[9];
    }

}
