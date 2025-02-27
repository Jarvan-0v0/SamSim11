package org.blkj.utils.base;

/*
 * RegexType.java
 * 
 * 常用正则表达式
 * 使用方法一、
 * ①构造一个模式.
 * Pattern p=Pattern.compile("[a-z]*");
 * ②建造一个匹配器
 * Matcher m = p.matcher(str);
 * ③进行判断，得到结果
 * boolean b = m.matches()；
 * 使用方法二、
 * boolean b=Pattern.matches(RegexType.chinese, "中国");
 */

/*
 * 
 * 正则表达式的各种符号及其含义
 * 
 * 常用的符号
 * . 表示任意一个字符
 * \s 空格字符(空格键, tab, 换行, 换页, 回车)
 * \S 非空格字符([^\s])
 * \d 一个数字，（相当于[0-9] ）
 * \D 一个非数字的字符，（相当于[^0-9] ）
 * \w 一个单词字符(word character) （相当于 [a-zA-Z_0-9] ）
 * \W 一个非单词的字符，[^\w]
 * ^ 一行的开始
 * $ 一行的结尾
 * \b 一个单词的边界
 * \B 一个非单词的边界
 * \G 前一个匹配的结束
 * [] 匹配方括号内的一个字符
 * 例如：[abc] 表示字符a，b，c中的任意一个(与a|b|c相同)
 * [a-zA-Z] 表示从a到z或A到Z当中的任意一个字符
 * 表示次数的符号
 *  重复零次或更多次
 * 例如：a* 匹配零个或者多个a
 * + 重复一次或更多次
 * 例如：a+ 匹配一个或者多个a
 * ? 重复零次或一次
 * 例如：a? 匹配零个或一个a
 * {n} 重复n次
 * 例如：a{4} 匹配4个a
 * {n,} 重复n次或更多次
 * 例如：a{4,} 匹配至少4个a
 * {n,m} 重复n到m次
 * 例如：a{4,10} 匹配4~10个a
 * 
 * @author hkm
 */
public interface RegexType {

    String english = "[a-zA-Z0-9_-]+$";//已测试
    String chinese = "[\u0391-\uFFE5]+$";//已测试
    String number = "[-+]?\\d+(\\.\\d+)?$";//已测试
    String integer = "[-+]?\\d+$";//已测试
    String floatReg = "[-+]?\\d+(.\\d+)?$";//已测试
    String chinaDate = "[0-9]{4}[-][0-9]{2}[-][0-9]{2}";////已测试//"^\\d{4}-\\d{2}-\\d{2}$";//"2010-10-31"// "(d{4})(-|/)(d{1,2})2(d{1,2})$";
    String email = "w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*$";//待测试
    //String url = "(((ht|f)tp(s?))://)[a-zA-Z0-9]+.[a-zA-Z0-9]+[/=?%-&_~`@[]':+!]*([^<>\"\"])*$";//待测试
    //String phone = "(((d{2,3}))|(d{3}-))?((0d{2,3})|0d{2,3}-)?[1-9]d{6,7}(-d{1,4})?$";//待测试
    //String mobile = "(((d{2,3}))|(d{3}-))?((13d{9})|(159d{8}))$";//待测试
    //String ip = "(0|[1-9]d?|[0-1]d{2}|2[0-4]d|25[0-5]).(0|[1-9]d?|[0-1]d{2}|2[0-4]d|25[0-5]).(0|[1-9]d?|[0-1]d{2}|2[0-4]d|25[0-5]).(0|[1-9]d?|[0-1]d{2}|2[0-4]d|25[0-5])$";//待测试
    //String zipcode = "[1-9]d{5}$";//待测试
    String qq = "[1-9]d{4,10}$";//待测试
    //String msn = "w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*$";//待测试
    //String idcard = "(^d{15}$)|(^d{17}[0-9Xx]$)";//待测试
    //String charbegin = "\\b[A-Za-z]\\w";//待测试

}
