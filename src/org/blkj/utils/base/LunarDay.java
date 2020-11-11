/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blkj.utils.base;



public class LunarDay {

    private long id = 0;//起始于1900-01-31的序号
    private long id_in_year = 0;//起始于1900-01-31的序号
    private long id_in_month = 0;//起始于1900-01-31的序号
    private String dayName = "初一";
    private String festival = "春节";
    private String memo = "";

    public LunarDay(int year, int month, int day,String dayName) {
        new LunarDay(year,month,day,dayName,"","");
    }

    public LunarDay(int year, int month, int day,String dayName,String festival) {
         new LunarDay(year,month,day,dayName,festival,"");
    }
    
    public LunarDay(int year, int month, int day,String dayName,String festival,String memo) {
        this.dayName=dayName;
        this.festival=festival;
        this.memo=memo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_in_year() {
        return id_in_year;
    }

    public void setId_in_year(long id_in_year) {
        this.id_in_year = id_in_year;
    }

    public long getId_in_month() {
        return id_in_month;
    }

    public void setId_in_month(long id_in_month) {
        this.id_in_month = id_in_month;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getFestival() {
        return festival;
    }

    public void setFestival(String festival) {
        this.festival = festival;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
