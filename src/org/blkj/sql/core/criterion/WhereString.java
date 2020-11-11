package org.blkj.sql.core.criterion;

import java.util.ArrayList;
import java.util.List;

import org.blkj.utils.StringTool;

/**
 * SQL标准语句中的where子句生成工具类。 使用Class工具，将符合JDBC
 * API字段映射类型的对象作为参数，生成SQL标准语句中的where子句
 *
 */
public class WhereString 
{
	public WhereString() {	}

	private List<String> statment = new ArrayList<String>();
	private List<String> subOp = new ArrayList<String>();
	
	public void setSubOp(List<String> subOp) {
		this.subOp = subOp;
	}
	public void setStatment(List<String> statment) {
		this.statment = statment;
	}
	public List<String> getStatment() {
		return statment;
	}

	public List<String> getSubOp() {
		return subOp;
	}
	
	//组装表达式形式：statment1 subOp statment2 。。。
	public String getWhereStr() 
	{	  
		if ( (statment == null) || (statment.size() == 0))
			return "";

		int subOpLength = 0;
		if (subOp != null)
			subOpLength = subOp.size();

		StringBuilder bufStr =  new StringBuilder();
		if (subOpLength == 0){//statment最多只能有一项,即表达式：statment1
			bufStr.append(statment.get(0));//只取第一项
		}
		else{//statment至少含有两个元素 ,即表达式：statment1 subOp statment2 。。。  
			int statmentLength = statment.size();
			if ( (statmentLength >= 2) && (subOpLength == (statmentLength-1))){
				//statment至少两项;subOp个数比statment的个数少一个
				int subCount = 0;
				for(int j = 0; j<statmentLength;){//处理statment
					if(subOpLength == j)//最后一次循环不执行
						break;
					if(bufStr.length() == 0){
						bufStr.append(statment.get(j)+ " ");
						bufStr.append(subOp.get(subCount++)+ " ");
						bufStr.append(statment.get(++j));
					}
					else{
						bufStr.append(" "+ subOp.get(subCount++)+ " ");
						bufStr.append(statment.get(++j));
					}
				}//处理statment
			}//if
		}
		if(bufStr.length() != 0)
			return bufStr.toString();
		else
			return "";
	}	

	
	
	
	/////////////////2017/////////////////2017/////////////////2017

////////////////////////////////////////////

	
	////////////////////////////////////////////////////////

	private String preoperator = "";
	private String field = "";
	private String operator = "";
	private Object value = null;
	private Object[] values = null;
	private QueryString subquery = null;
	private StringTool tool = new StringTool();
	private String where = "WHERE";

	/**
	 * 通过setField(field)、setOperator(operator)、setValue(value)方法设定where子句中的各个组成部分.
	 *
	 */

	/**
	 * 通过setField(field)、setOperator(operator)、setValue(value)方法设定where子句中的各个组成部分.
	 * field是字段名；operator是操作符，如：=、is、like等；value是字段类型的值对象
	 */
	public WhereString(String field, String operator, Object value) {
		this.field = field;
		this.operator = operator;
		this.value = value;
		this.values = null;
		this.subquery = null;
	}

	/**
	 * 通过setField(field)、setOperator(operator)、setValue(value)设定where子句中的各个组成部分.
	 * field是字段名；operator是操作符，如：=、is、like等；values是字段类型的值对象数组
	 *
	 */
	public WhereString(String field, String operator, Object[] values) {
		this.field = field;
		this.operator = operator;
		this.values = values;
		this.value = null;
		this.subquery = null;
	}

	/**
	 * 通过setField(field)、setOperator(operator)、setValue(value)设定where子句中的各个组成部分.
	 * field是字段名；operator是操作符，如：=、is、like等；subquery将生成一个子查询
	 */
	public WhereString(String field, String operator, QueryString subquery) {
		this.field = field;
		this.operator = operator;
		this.subquery = subquery;
		this.values = null;
		this.value = null;
	}

	/**
	 * 将不同的符合JDBC API映射的字段类型的值对象转化为字符串，使其成为SQL中标准where子句的值。
	 *
	 */
	private String objValuesToString(Object[] obj) {
		String v = "null";
		if (obj == null) {
			return v;    //如果对象为空,返回无单引号的null字符串
		}
		String clsName = obj[0].getClass().getName();
		int length = 0;
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				length++; //非空值数量
			}
		}
		if (length == 0) {
			return v;  //整个对象数组的元素均为空时,返回无单引号的null字符串
		} else {
			if ((!clsName.equals("java.lang.String"))
					&& (!clsName.equals("java.lang.Integer"))
					&& (!clsName.equals("java.lang.Float"))
					&& (!clsName.equals("java.lang.Double"))
					&& (!clsName.equals("java.lang.Boolean"))
					&& (!clsName.equals("java.lang.Short"))
					&& (!clsName.equals("java.lang.Long"))
					&& (!clsName.equals("java.sql.Date"))
					&& (!clsName.equals("java.sql.Time"))
					&& (!clsName.equals("java.sql.Timestamp"))) {
				return v;     //如果不符合规定类型返回无单引号的null字符串
			}

			String[] _obj = new String[length];
			int j = 0;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null) {
					_obj[j] = obj[i].toString(); //过滤空值
					j++;
				}
			}

			if (clsName.equals("java.lang.Integer") || clsName.equals("java.lang.Float") || clsName.equals("java.lang.Long") || clsName.equals("java.lang.Double") || clsName.equals("java.lang.Short")) {
				//数字型均不加单引号
				v = tool.arryToString(_obj, ",");
				return v;
			}

			if (clsName.equals("java.lang.Boolean")) {
				//不知是否加上单引号,请帮个忙
				v = tool.arryToString(_obj, ",");
				return v;
			}

			if (clsName.equals("java.lang.String") || clsName.equals("java.sql.Date") || clsName.equals("java.sql.Time") || clsName.equals("java.sql.Timestamp")) {
				//字符串型均加上单引号
				_obj = tool.arryToArray(_obj, "'", "'");
				v = tool.arryToString(_obj, ",");
				return v;
			}

		}
		return v;
	}

	/**
	 * 核心方法，将不同的符合JDBC API映射的字段类型的值对象转化为字符串，使其成为SQL中标准where子句的值。
	 *
	 */
	private String objValueToString(Object objValue) {
		String v = "null";
		if (objValue == null) {
			return v;      //如果对象为空,返回无单引号的null字符串
		}
		String clsName = objValue.getClass().getName();

		if ((!clsName.equals("java.lang.String"))
				&& (!clsName.equals("java.lang.Integer"))
				&& (!clsName.equals("java.lang.Float"))
				&& (!clsName.equals("java.lang.Double"))
				&& (!clsName.equals("java.lang.Boolean"))
				&& (!clsName.equals("java.lang.Short"))
				&& (!clsName.equals("java.lang.Long"))
				&& (!clsName.equals("java.sql.Date"))
				&& (!clsName.equals("java.sql.Time"))
				&& (!clsName.equals("java.sql.Timestamp"))) {
			return v;//如果不符合规定类型返回无单引号的null字符串
		}
		if (clsName.equals("java.lang.Integer") || clsName.equals("java.lang.Float") || clsName.equals("java.lang.Long") || clsName.equals("java.lang.Double") || clsName.equals("java.lang.Short")) {
			return objValue.toString();//数字型均不须加上单引号
		}
		if (clsName.equals("java.lang.Boolean")) {
			return objValue.toString();//不知是否加上单引号,请帮个忙
		}
		v="'" + objValue.toString() + "'";//字符串型均加上单引号
		return v;
	}

	public void setSubquery(QueryString subquery) {
		this.subquery = subquery;
		this.values = null;
		this.value = null;
	}
	public void setValue(Object value) {
		this.value = value;
		this.values = null;
		this.subquery = null;
	}
	public void setValues(Object[] values) {
		this.values = values;
		this.value = null;
		this.subquery = null;
	}
	public Object getValue() {
		return value;
	}
	public Object[] getValues() {
		return values;
	}

	public QueryString getSubquery() {
		return subquery;
	}

	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}

	public void setPreoperator(String preoperator) {
		this.preoperator = preoperator;
	}
	public String getPreoperator() {
		return preoperator;
	}

	public void setField(String field) {
		this.field = field;
	}
	public String getField() {
		return field;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperator() {
		return operator;
	}
	
	/**
	 * 生成一个完整的SQL标准语句中的where子句。
	 *
	 */
	public String getWhereSql() 
	{

		String v = "";
		if (this.getSubquery() != null) {
			v = "(" + this.getSubquery().toString() + ")";
		}

		if (this.getValues() != null) {
			if (this.getValues().length > 1) {
				v = "(" + this.objValuesToString(this.getValues()) + ")";
			} else {
				v = this.objValuesToString(this.getValues());
			}
		}
		if (this.getValue() != null) {
			v = this.objValueToString(this.getValue());
		}

		String pre = "";
		if ("".equals(this.getPreoperator())) {
			pre = this.getWhere();
		} else {
			pre = this.getPreoperator();
		}
		String where = "";
		if ( pre.equals("") || this.getOperator().equals("") || 
				v.equals("") || this.getField().equals("")) {
			return where;
		}
		where = " " + pre + " (" + this.getField() + " " + this.getOperator() + " " + v + ") ";
		return where;
	} 
}

/**
public String getWhereSql(List<IdentityHashMap<String,IdentityHashMap<String,Object>>> list) 
{
	//遍历
	for (IdentityHashMap<String,IdentityHashMap<String,Object>> map : list) 
	{////for 1
		StringBuilder bufStr =  new StringBuilder();
		for (Entry<String,IdentityHashMap<String,Object>> entryL1: map.entrySet()) 
		{//for 2
			String opL1 = entryL1.getKey();

			IdentityHashMap<String,Object> conL1 = entryL1.getValue();
			for (Entry<String,Object> entryL2: conL1.entrySet()) 
			{//for 3
				String temp = "";	 
				temp = " (" + entryL2.getKey() + "'"+ entryL2.getValue() + "') ";
				if (bufStr.length() == 0)
				{
					bufStr = bufStr.append("WHERE");
					bufStr = bufStr.append(temp);
				}
				else
				{
					bufStr = bufStr.append(opL1);
					bufStr = bufStr.append(temp);
				}
			} //for 3
			MyLogger.showMessage("bufStr:" + bufStr.toString());
		}//for 2

	}//for 1

	return where;
}*/
