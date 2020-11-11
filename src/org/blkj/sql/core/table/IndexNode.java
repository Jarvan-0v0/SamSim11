/*
 * 索引节点
 */
package org.blkj.sql.core.table;

public class IndexNode 
{
    /**
     * 数据库表中第一个主关键字段的值，如果没有关键字段，则是第一个字段的值
     */
    private Object firstKeyValue;//
    /**
     * 按照主关键字顺序查询得到的行号，在JDBC中，第一条记录的行号是1，行号与长度完全相等；第一条前的和最后一条后的行号均为0。
     */
    private int row;
    
    public IndexNode(){}

    public Object getFirstKeyValue() {
        return firstKeyValue;
    }

    public void setFirstKeyValue(Object firstKeyValue) {
        this.firstKeyValue = firstKeyValue;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
