package org.blkj.sql.core.table;
/**
 * 管理数据库表中字段的相关信息，如：字段名、字段类型、默认值等等
 */
public class Field 
{
    private String name = "";//字段名
    private String typeName = "";//字段类型(如：bigint、char)
    private int sqlType = -1;//字段的java.sql.Types的值java.sql.Types.BIGINT
    private String typeClassName = "";//字段类型对应的Java类的名称
    private String position = "";//字段允许长度,在DatabaseMetaData中均用String表示
    private String size = "";//字段长度,在DatabaseMetaData中均用String表示//获取COLUMN_SIZE
    private String decimal = "";//字段精度
    private String defaultValue = "";//字段默认值
    private String remark = "";//掩码,部分数据库支持
    private String format = "";//其值将由系统自动生成,故用此字段格式化或正则表达式来代替remark
    private String regex = "";//由用户填写正则表达式
    private String errmsg = "";//录入字段错误信息
    private String commend = "";//说明,注释
    private String bufferLength="";//BUFFER_LENGTH
    private boolean nullable = false;//是否允许为空值，true允许，false不允许
    private boolean primarykey = false;//是否是主键
    private boolean foreignkey = false;//是否是外键键
  
	public Field(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String type) {
        this.typeName = type;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }
    
    

    public String getTypeClassName() {
        return typeClassName;
    }

    public void setTypeClassName(String typeClassName) {
        this.typeClassName = typeClassName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

   

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDecimal() {
        return decimal;
    }

    public void setDecimal(String decimal) {
        this.decimal = decimal;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimarykey() {
        return primarykey;
    }

    public void setPrimarykey(boolean primarykey) {
        this.primarykey = primarykey;
    }


    public boolean isForeignkey() {
        return foreignkey;
    }

    public void setForeignkey(boolean foreignkey) {
        this.foreignkey = foreignkey;
    }

    public String getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(String bufferLength) {
        this.bufferLength = bufferLength;
    }

}
