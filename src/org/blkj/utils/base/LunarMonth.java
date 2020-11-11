/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blkj.utils.base;

import java.util.ArrayList;
import java.util.List;

public class LunarMonth {

    private int id = 0;//在1900-01-31年后的农历月序号
    private int month_id = 0;//在当年年中的序号
    private int daysize = 29;//月中的天数
    private boolean isLeapMonth = false;//是否是闰月
    private String monthName = "一月";
    private List<LunarDay> days=new ArrayList<LunarDay>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth_id() {
        return month_id;
    }

    public void setMonth_id(int month_id) {
        this.month_id = month_id;
    }

    public int getDaysize() {
        return daysize;
    }

    public void setDaysize(int daysize) {
        this.daysize = daysize;
    }

    public boolean isIsLeapMonth() {
        return isLeapMonth;
    }

    public void setIsLeapMonth(boolean isLeapMonth) {
        this.isLeapMonth = isLeapMonth;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public List<LunarDay> getDays() {
        return days;
    }

    public void setDays(List<LunarDay> days) {
        this.days = days;
    }

    
}
