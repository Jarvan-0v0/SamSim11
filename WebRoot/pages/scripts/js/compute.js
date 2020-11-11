//计算文件大小函数(保留两位小数),Size为字节大小
function CountFileSize(Size) {
	var m_strSize = " ";
	var FactSize = 0;
	FactSize = Size;
	var suf = " KB";
	if (FactSize < 1024){
		m_strSize = FactSize + " ";// + " 字节";
		suf = " 字节";
	}
	else if (FactSize >= 1024 && FactSize < 1048576)
		m_strSize = (FactSize / 1024) + " ";// + " KB";
	else if (FactSize >= 1048576 && FactSize < 1073741824)
	{
		m_strSize = (FactSize / 1024 / 1024) + " ";// + " MB";
		suf = " MB";
	}
	else if (FactSize >= 1073741824)
	{
		m_strSize = (FactSize / 1024 / 1024 / 1024) + " ";// + " GB";
		suf = " GB";
	};
	var pos = m_strSize.indexOf(".");
	if (pos > 0)
		return (m_strSize.substr(0,pos+3) + suf);
	else
		return m_strSize + suf;
}

/////////////////////////////////////////
/**
当前时间
 */
function CurrentTime() {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var hour = date.getHours();
	var minute = date.getMinutes();
	var second = date.getSeconds();
	return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
}
//设置当前年
function CurrentYear(){  
	var date = new Date();  
	return date.getFullYear();  
}
//设置当前日期 不含时分秒
function CurrentDate(){  
	var date = new Date();  
	var seperator1 = "-";  
	var seperator2 = ":";  
	var month = date.getMonth() + 1;  
	var strDate = date.getDate();  
	if(month >= 1 && month <= 9) month = "0" + month;  
	if(strDate >= 0 && strDate <= 9) strDate = "0" + strDate;  
	var end = date.getFullYear() + seperator1 + month + seperator1 + strDate;  
	return end;  
}  
//设置当前日期  含有时分秒
function CurrentDateTime(){  
	var date = new Date();  
	var seperator1 = "-";  
	var seperator2 = ":";  
	var month = date.getMonth() + 1;  
	var strDate = date.getDate();  
	if(month >= 1 && month <= 9) month = "0" + month;  
	if(strDate >= 0 && strDate <= 9) strDate = "0" + strDate;  
	var end = date.getFullYear() + seperator1 + month + seperator1 + strDate +
	   " " + date.getHours() + seperator2 + date.getMinutes() + seperator2 + date.getSeconds();  
	return end;  

	/**设置开始日期为当前日期的前一个月  
    date.setMonth(date.getMonth()-1);  
    var begin = date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate()+  
                " " + date.getHours() + seperator2 + date.getMinutes() +seperator2 + date.getSeconds();  
	 */
}  
//获取当前时间前后N天前后日期的方法
function getDateBeforeNow(AddDayCount) {   
	var dd = new Date();  
	dd.setDate(dd.getDate()+AddDayCount); 
	var y = dd.getFullYear();   
	var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0  
	var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0  
	return y+"-"+m+"-"+d;   
}  
//得到n个月之前的日期（今天）
function getBeforeMonthsDate(month) 
{   
	var dd = new Date();  
	dd.setMonth( dd.getMonth() - month) 
	var y = dd.getFullYear();   
	var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0  
	var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate();//获取当前几号，不足10补0  
	return y+"-"+m+"-"+d;   
}  

//服务器当前日期
function ServerCurrentTime()
{
	var now = new Date();
	var year = now.getFullYear();
	var month = now.getMonth();
	var date = now.getDate();
	var day = now.getDay();
	var hour = now.getHours();
	var minu = now.getMinutes();
	var sec = now.getSeconds();
	var week;
	month = month + 1;
	if (month < 10) month = "0" + month;
	if (date < 10) date = "0" + date;
	if (hour < 10) hour = "0" + hour;
	if (minu < 10) minu = "0" + minu;
	if (sec < 10) sec = "0" + sec;
	var arr_week = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
	week = arr_week[day];
	var time = "";
	time = year + "年" + month + "月" + date + "日" + " " + hour + ":" + minu + ":" + sec;
	$("#CurrentTime").text(time);
	var timer = setTimeout("ServerCurrentTime()", 1000);
}

/**
* 金额格式(保留2位小数)后格式化成金额形式
*/
function FormatCurrency(num) {
    num = num.toString().replace(/\$|\,/g, '');
    if (isNaN(num))
        num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num * 100 + 0.50000000001);
    cents = num % 100;
    num = Math.floor(num / 100).toString();
    if (cents < 10)
        cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3) ; i++)
        num = num.substring(0, num.length - (4 * i + 3)) + '' +
                num.substring(num.length - (4 * i + 3));
    return (((sign) ? '' : '-') + num + '.' + cents);
}
/**
格式化时间显示方式、用法:format="yyyy-MM-dd hh:mm:ss";
*/
formatDate = function (v, format) {
    if (!v) return "";
    var d = v;
    if (typeof v === 'string') {
        if (v.indexOf("/Date(") > -1)
            d = new Date(parseInt(v.replace("/Date(", "").replace(")/", ""), 10));
        else
            d = new Date(Date.parse(v.replace(/-/g, "/").replace("T", " ").split(".")[0]));//.split(".")[0] 用来处理出现毫秒的情况，截取掉.xxx，否则会出错
    }
    var o = {
        "M+": d.getMonth() + 1,  //month
        "d+": d.getDate(),       //day
        "h+": d.getHours(),      //hour
        "m+": d.getMinutes(),    //minute
        "s+": d.getSeconds(),    //second
        "q+": Math.floor((d.getMonth() + 3) / 3),  //quarter
        "S": d.getMilliseconds() //millisecond
    };
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (d.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};