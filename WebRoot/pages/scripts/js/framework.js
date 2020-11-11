/*	window.event 只能在IE下运行，而不能在Firefox下运行，这是因为Firefox的event只能在事件发生的现场使用。
	兼容不同浏览器的 TypeError: event is undefined的解决方案
	改变浏览器窗口大小时,此函数执行 目前没有用
	window.onresize=function(){location.reload();};
	var theEvent = window.event || arguments.callee.caller.arguments[0];
	var srcElement = theEvent.srcElement;
	if (!srcElement) {
	       srcElement = theEvent.target;
	}

在火狐浏览器下可以使用arguments.callee.caller.arguments[0]。既
var event = window.event || arguments.callee.caller.arguments[0]
console.log( event.type )就可以获取火狐浏览器的事件信息了
}  
（每次用事件之前Firefox都需要用getEvent()获取一下，否则就是

  alertDialog("为保证系统效率,只允许同时运行10个功能窗口,请关闭一些窗口后重试！", 0)
 */


//样式
$(function () {
	readyIndex();
})
function readyIndex() {
	$(".main_menu li div").click(function () {
		$(".main_menu li div").removeClass('main_menu leftselected');
		$(this).addClass('main_menu leftselected');
	}).hover(function () {
		$(this).addClass("hoverleftselected");
	}, function () {
		$(this).removeClass("hoverleftselected");
	});
}


/**
查找数组中是否存在某个值
arr:数组
val:对象值
 **/
function ArrayExists(arr, val) {
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] == val)
			return true;
	}
	return false;
}
//自动补全表格
var IndetableRow_autocomplete = 0;
var scrollTopheight = 0;
function autocomplete(Objkey, width, height, data, callBack) {
	if ($('#' + Objkey).attr('readonly') == 'readonly') {
		return false;
	}
	if ($('#' + Objkey).attr('disabled') == 'disabled') {
		return false;
	}
	IndetableRow_autocomplete = 0;
	scrollTopheight = 0;
	var X = $("#" + Objkey).offset().top;
	var Y = $("#" + Objkey).offset().left;
	$("#div_gridshow").html("");
	if ($("#div_gridshow").attr("id") == undefined) {
		$('body').append('<div id="div_gridshow" style="overflow: auto;z-index: 1000;border: 1px solid #A8A8A8;width:' + width + ';height:' + height + ';position: absolute; background-color: #fff; display: none;"></div>');
	} else {
		$("#div_gridshow").height(height);
		$("#div_gridshow").width(width);
	}
	var sbhtml = '<table class="grid" style="width: 100%;">';
	if (data != "") {
		sbhtml += '<tbody>' + data + '</tbody>';
	} else {
		sbhtml += '<tbody><tr><td style="color:red;text-align:center;width:' + width + ';">没有找到您要的相关数据！</td></tr></tbody>';
	}
	sbhtml += '</table>';
	$("#div_gridshow").html(sbhtml);
	$("#div_gridshow").css("left", Y).css("top", X + $('#' + Objkey).height()).show();
	$("#div_gridshow .grid td").css("border-left", "none").css("padding-left", "2px");
	if (data != "") {
		$("#div_gridshow").find('tbody tr').each(function (r) {
			if (r == 0) {
				$(this).addClass('selected');
			}
		});
	}
	$("#div_gridshow").find('tbody tr').click(function () {
		var parameter = "";
		$(this).find('td').each(function (i) {
			parameter += '"' + $(this).attr('id') + '"' + ':' + '"' + $.trim($(this).text()) + '",'
		});
		if ($('#' + Objkey).attr('readonly') == 'readonly') {
			return false;
		}
		if ($('#' + Objkey).attr('disabled') == 'disabled') {
			return false;
		}
		callBack(JSON.parse('{' + parameter.substr(0, parameter.length - 1) + '}'));
		$("#div_gridshow").hide();
	});
	$("#div_gridshow").find('tbody tr').hover(function () {
		$(this).addClass("selected");
	}, function () {
		$(this).removeClass("selected");
	});
	//任意键关闭
	document.onclick = function (e) {
		var e = e ? e : window.event;
		var tar = e.srcElement || e.target;
		if (tar.id != 'div_gridshow') {
			if ($(tar).attr("id") == 'div_gridshow' || $(tar).attr("id") == Objkey) {
				$("#div_gridshow").show();
			} else {
				$("#div_gridshow").hide();
			}
		}
	}
}
//方向键上,方向键下,回车键
function autocompletekeydown(Objkey, callBack) {
	$("#" + Objkey).keydown(function (e) {
		switch (e.keyCode) {
		case 38: // 方向键上
			if (IndetableRow_autocomplete > 0) {
				IndetableRow_autocomplete--
				$("#div_gridshow").find('tbody tr').removeClass('selected');
				$("#div_gridshow").find('tbody tr').each(function (r) {
					if (r == IndetableRow_autocomplete) {
						scrollTopheight -= 22;
						$("#div_gridshow").scrollTop(scrollTopheight);
						$(this).addClass('selected');
					}
				});
			}
			break;
		case 40: // 方向键下
			var tindex = $("#div_gridshow").find('tbody tr').length - 1;
			if (IndetableRow_autocomplete < tindex) {
				IndetableRow_autocomplete++;
				$("#div_gridshow").find('tbody tr').removeClass('selected');
				$("#div_gridshow").find('tbody tr').each(function (r) {
					if (r == IndetableRow_autocomplete) {
						scrollTopheight += 22;
						$("#div_gridshow").scrollTop(scrollTopheight);
						$(this).addClass('selected');
					}
				});
			}
			break;
		case 13:  //回车键
			var parameter = "";
			$("#div_gridshow").find('tbody tr').each(function (r) {
				if (r == IndetableRow_autocomplete) {
					$(this).find('td').each(function (i) {
						parameter += '"' + $(this).attr('id') + '"' + ':' + '"' + $.trim($(this).text()) + '",'
					});
				}
			});
			if ($('#' + Objkey).attr('readonly') == 'readonly') {
				return false;
			}
			if ($('#' + Objkey).attr('disabled') == 'disabled') {
				return false;
			}
			callBack(JSON.parse('{' + parameter.substr(0, parameter.length - 1) + '}'));
			$("#div_gridshow").hide();
			break;
		default:
			break;
		}
	})
}
/*
警告消息
msg: 显示消息
type：类型 >1：成功，<1：失败，其他：警告
 */
function alertDialog(msg, type)
{   //var _title = "关于";
	var msg = "<div class='ui_alert'>" + msg + "</div>"
	var icon = "";
	if (type >= 1) {
		icon = "succ.png";
	} else if (type == -1) {
		icon = "fail.png";
	} else {
		icon = "i.png";
	}
	top.$.dialog({
		id: "alertDialog",
		icon: icon,
		content: msg,
		title: "提示信息",
		ok: function () {
			return true;
		}
	});
}

function aboutDialog(title, msg, type)
{   //var _title = "关于";
	var msg = "<div class='ui_alert'>" + msg + "</div>"
	var icon = "";
	if (type >= 1) {
		icon = "succ.png";
	} else if (type == -1) {
		icon = "fail.png";
	} else {
		icon = "i.png";
	}
	top.$.dialog({
		id: "alertDialog",
		icon: icon,
		content: msg,
		title: title,
	});
}

/*
   必须回答问题的弹出对话框，即只有确认按钮
 */
function ansDialog(url, _id, _title, _width, _height, callBack) 
{
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		title: _title,
		max: false,
		min: false,
		lock: true,
		resize: false,
		extendDrag: true,
		content: 'url:' + RootPath() + url,
		ok: function () {
			callBack(_id);
			return false;
		},
		cancel: false
	});
}

//=================动态菜单tab标签========================
function AddTabMenu(tabid, url, name, img, Isclose, IsReplace, IsVisitorModule) 
{
	$('#overlay_startmenu').hide(); $('#start_menu_panel').hide(); 
	$('#start_menu_panel.nicescroll-rails').show(); $('.nicescroll-rails').hide();
	if (url == "" || url == "#" || url == RootPath()) {
		url = RootPath() + "/pages/common/Error404.html";//"./"
	}

	var urlPart = url.split("/");
	if(urlPart[0] != "http:")	//[ljw修改 使子窗口可以加载jsp等外部目录文件]
		url = RootPath() + url;//增加XBM

	var tabs_container = top.$("#tabs_container");
	var ContentPannel = top.$("#ContentPannel");
	if (IsReplace == 'true' || IsReplace == true) {
		top.RemoveDiv(tabid);
	}
	if (top.document.getElementById("tabs_" + tabid) == null) { //如果当前tabid存在直接显示已经打开的tab
		Loading(true);
		if (!IsVisitorModule) {
			VisitorModule(tabid, name);
		}
		if (tabs_container.find('li').length >= 9) {
			Loading(false);
			alertDialog("为保证系统效率,只允许同时运行9个功能窗口,请关闭一些窗口后重试！", 0)
			return false;
		}
		tabs_container.find('li').removeClass('selected');
		ContentPannel.find('iframe').hide();

		//写到右提示条
		if (Isclose != 'false') { //判断是否带关闭tab
			tabs_container.append("<li id=\"tabs_" + tabid + "\" class='selected' win_close='true'><span title='" + name + "' onclick=\"AddTabMenu('" + tabid + "','" + url + "','" + name + "','true')\"><a><img src='../scripts/images/Icon16/" + img + "' width='16' height='16'>" + name + "</a></span><a class=\"win_close\" title=\"关闭当前窗口\" onclick=\"RemoveDiv('" + tabid + "')\"></a></li>");
		} else {
			tabs_container.append("<li id=\"tabs_" + tabid + "\" class=\"selected\" onclick=\"AddTabMenu('" + tabid + "','" + url + "','" + name + "','false')\"><a><img src='../scripts/images/Icon16/" + img + "' width='16' height='16'>" + name + "</a></li>");
		}
		//在ContentPannel中显示页面内容
		//alert("tabid:"+("<iframe id=\"tabs_iframe_" + tabid + "\" name=\"tabs_iframe_" + tabid + "\" height=\"100%\" width=\"100%\" src=\"" + url + "\" frameBorder=\"0\"></iframe>"));
		ContentPannel.append("<iframe id=\"tabs_iframe_" + tabid + "\" name=\"tabs_iframe_" + tabid + "\" height=\"100%\" width=\"100%\" src=\"" + url + "\" frameBorder=\"0\"></iframe>");
	} else {
		if (!IsVisitorModule) {
			VisitorModule(tabid, name);
		}
		tabs_container.find('li').removeClass('selected');
		ContentPannel.find('iframe').hide();
		tabs_container.find('#tabs_' + tabid).addClass('selected');
		top.document.getElementById("tabs_iframe_" + tabid).style.display = 'block';
	}
	$('iframe#' + tabiframeId()).load(function () {
		Loading(false);
	});
	LoadrightMenu(".tab-nav li");
}

/* url: RootPath() + url,*/
function AjaxJson(url, postData, callBack) 
{
	try {
		$.ajax({
			url: RootPath() + url,
			type: "post",
			data: postData,
			dataType: "json",
			async: false,
			success: function (data) {
				if (data.Code == "-1") {
					Loading(false);
					alertDialog(data.Message, -1);
				} else {
					Loading(false);
					callBack(data);
				}
			},
			error: function (data) {
				Loading(false);
				alertDialog(data.responseText, -1);
			}
		});
	} catch (e) { }
}


//全部关闭tab
function AllcloseTab() {
	top.$(".tab-nav").find("[win_close=true]").each(function () {
		RemoveDiv($(this).attr('id').substr(5))
	});
}

//关闭当前tab
function btn_back() {
	top.ThisCloseTab();
}

/*绑定数据字典下拉框
ControlId:控件ID
Code:分类编码
Memo:默认显示
用于select
 */
function BindDropItem(url, ControlId, Code,Memo) {
	$(ControlId).html("");
	if (IsNullOrEmpty(Memo)) {
		$(ControlId).append("<option value=" + Memo + ">" + Memo + "</option>");
	}
	AjaxJson(url, "", function (DataJson) {//getAjax
		$.each(DataJson, function (i) {
			var Json = JSON.stringify(DataJson[i]);
			var obj = eval("("+Json+")");//转换后的JSON对象
			if (obj.dropID == Code){ 
				$(ControlId).append($("<option></option>").val(DataJson[i].itemid).html(DataJson[i].fullname));
			}
		});
		Loading(false);
	});
}
/* 用于select 以外情况*/
function BindItem(url, ControlId, DropID, ItemID) {
	AjaxJson(url, "", function (DataJson) {//getAjax
		$.each(DataJson, function (i) {
			var Json = JSON.stringify(DataJson[i]);
			var obj = eval("("+Json+")");//转换后的JSON对象
			if ( (obj.dropID == DropID) && (DataJson[i].itemid == ItemID) ) {
				$(ControlId).val(DataJson[i].fullname);
			}
		});
		Loading(false);
	});
}

/*关闭对话框*/
function closeDialog() {
	var api = frameElement.api,//内容页中调用窗口实例对象接口
	W = api.opener;//加载窗口组件页面的window对象
	api.close();
	return true;
}

/**
树下拉框
Objkey:          ID
width：          宽度
height：         高度
data：           数据
 **/
function comboBoxTree(Objkey, height) {
	top.$(".ui_state_highlight").focus();
	var width = $("#" + Objkey).width();
	$("#" + Objkey).css('ime-mode', 'disabled');
	$("#" + Objkey).bind("contextmenu", function () { return false; });
	$("#" + Objkey).keypress(function (e) { return false; });
	if ($('#' + Objkey).attr('readonly') == 'readonly') { return false; }
	if ($('#' + Objkey).attr('disabled') == 'disabled') { return false; }
	var X = $("#" + Objkey).offset().top - 1;
	var Y = $("#" + Objkey).offset().left - 1;
	var comboBoxTree = "comboBoxTree" + Objkey;
	if ($("#" + comboBoxTree).attr("id") == undefined) {
		$('body').append('<div id="' + comboBoxTree + '" style="overflow: auto;border: 1px solid #ccc;border-top:none;width:' + width + 'px;height:' + height + ';position: absolute; background-color: #fff; display: none;"></div>');
	}
	$("#" + comboBoxTree).css("left", Y).css("top", X + $("#" + Objkey).height() + 2).css("z-index", "99").slideDown(100);
	//任意键关闭
	document.onclick = function (e) {
		var e = e ? e : window.event;
		var tar = e.srcElement || e.target;
		if (tar.id != '' + comboBoxTree + '') {
			if ($(tar).hasClass("bbit-tree-ec-icon")) {
				return false;
			}
			if ($(tar).attr("id") == Objkey) {
				return false;
			} else {
				$("#" + comboBoxTree).slideUp(100);
			}
		}
	}
}

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

/*
确认对话框
 */
function confirmDialog(_title, msg, callBack) {
	var msg = "<div class='ui_alert'>" + msg + "</div>"
	top.$.dialog({
		id: "confirmDialog",
		lock: true,
		icon: "hits.png",
		content: msg,
		title: _title,
		ok: function () {
			callBack(true)
			return true;
		},
		cancel: function () {
			callBack(false)
			return true;
		}
	});
}

function confirmDialog2(msg, callBack) 
{
	swal({
		width: '400px',
		text: msg,
		showCancelButton: true,
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		confirmButtonColor: '#3085d6',
	}).then(function() {
		callBack(true);
		return true;
	}, function(dismiss) {
		callBack(false);
		return true;
	});
}
//???
function confirmDialogH5(msg, callBack) 
{
	$.dialog.confirm(msg, function ()	{
		callBack(true);
		return true;
	}, function () {
		callBack(false);
		return true;
	});
}

/*
表格视图列名 ??
 */
function columnModelData(jgrid) {
	AjaxJson("/Utility/LoadViewColumn", {}, function (data) {
		$.each(data, function (i) {
			$(jgrid).hideCol(data[i].FieldName);//如果参数为一个列名则隐藏此列，如果给定的是数组则隐藏指定的所有列。
		});
	});
}

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

function CurrentDate() {
	var date = new Date();
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	return year + '-' + month + '-' + day ;
}
function CurrentOnlyTime() {
	var date = new Date();
	var hour = date.getHours();
	var minute = date.getMinutes();
	var second = date.getSeconds();
	return hour + ':' + minute + ':' + second;
}

/**
获取选中复选框值
值：1,2,3,4
 **/
function CheckboxValue() {
	var reVal = '';
	$('[type = checkbox]').each(function () {
		if ($(this).attr("checked")) {
			reVal += $(this).val() + ",";
		}
	});
	reVal = reVal.substr(0, reVal.length - 1);
	return reVal;
}
/*
弹出对话框（没按钮）
 */
function Dialog(url, _id, _title, _width, _height)
{
	Loading(true);
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		max: false,
		lock: true,
		title: _title,
		content: 'url:' + RootPath() + url
	});
}

/*删除数据
/*url:   表示请求路径
/*parm：      条件参数
--------------------------------------------------*/
function delConfirm(url, parm, msg) {
	confirmDialog("提示", msg, function (r) {
		if (r) {
			Loading(true, "正在删除数据...");
			window.setTimeout(function () {
				getAjaxJson(url, parm, function (data) {//AjaxJson
					tipDialog(data.Message, 3, data.Code);
					if (data.Code > 0) {
						windowload();
					}
				});
			}, 200);
		}
	});
}
function delConfig(url, parm, count) {
	if (count == undefined) {
		count = 1;
	}
	confirmDialog("提示", "您确定要删除 " + count + " 笔记录？", function (r) {
		if (r) {
			Loading(true, "正在删除数据...");
			window.setTimeout(function () {
				getAjaxJson(url, parm, function (data) {//AjaxJson
					tipDialog(data.Message, 3, data.Code);
					if (data.Code > 0) {
						windowload();
					}
				});
			}, 200);
		}
	});
}

/*
验证对象值不能重复 
id:字段ID、
tablename：表名、
keyfield：主键Id、
Msg：提示消息 this.id,'user','id','工号'
 */
function FieldExist(id, tablename, keyfield, displayFlag, Msg) {
	var $ObjFiel = $("#" + id);
	var postData = {
			tablename: tablename,
			fieldname: keyfield,
			fieldvalue: $ObjFiel.val()
	}
	var op = "true";
	getAjaxJson('/fieldExist.do', postData, function (data) {//getAjax
		if (data.Status.toLocaleLowerCase() == 'true') {//data.Status 存在返回true
			if (displayFlag == 'true'){
				tipCss("#" + id, Msg + "已存在,请重新输入！");
				op = "false";
			}
		}
		else if (displayFlag == 'false'){
			tipCss("#" + id, Msg + "不存在,请先录入相关信息！");
			op = "false";
			//$($ObjFiel).val("");    
		}
		Loading(false);

	});
	if(op == "false"){
		setTimeout(function(){$($ObjFiel).val("");}, 5000);
	}
}
/*
  setTimeout(funcName,500);function funcName() {  xxxxxx;} 0.5秒后执行funcName(),只执行一次
  setInterval(funcName,5000);   每隔5秒执行一次funcName()
 */
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

/*
最大化弹出对话框（没按钮）
 */
function FullDialog(url, _id, _title) {
	Loading(true);
	top.$.dialog({
		id: _id,
		lock: true,
		title: _title,
		max: false,
		min: false,
		width: top.$(window).width() - 40,
		height: top.$('body').height() - 100,
		content: 'url:' + RootPath() + url
	})
}
/*
指定大小 没按钮
 */
function FullDialog(url, _id, _title, _width, _height)
{
	Loading(true);
	top.$.dialog({
		id: _id,
		lock: true,
		title: _title,
		resize: true,
		width: _width,
		height: _height,
		max: false,
		content: 'url:' + RootPath() + url
	});
}

/*
最大化弹出对话框（带：确认按钮、取消按钮）
 */
function FullopenDialog(url, _id, _title, callBack) {
	Loading(true);
	top.$.dialog({
		id: _id,
		lock: true,
		title: _title,
		max: false,
		min: false,
		width: top.$(window).width() - 40,
		height: top.$('body').height() - 100,
		content: 'url:' + RootPath() + url,
		ok: function () {
			callBack(_id);
			return false;
		},
		cancel: true
	})
}

function FullWidthDialog(url, _id, _title, _height)
{
	Loading(true);
	top.$.dialog({
		id: _id,
		width: top.$(window).width()- 40,
		height: top.$('body').height() - 150,//height: _height,
		max: false,
		lock: true,
		title: _title,
		content: 'url:' + RootPath() + url
	});
}

/**
 * 获取搜索条件：返回JSON
 * var parameter = GetParameterJson("搜索区域table的ID");
 */
function GetParameterJson(tableId) {
	var parameter = "";
	$("#" + tableId + " tr").find('td').find('input,select').each(function () {
		var pk_id = $(this).attr('id');
		var pk_value = $("#" + pk_id).val();
		parameter += '"' + pk_id + '"' + ':' + '"' + $.trim(pk_value) + '",'
	});
	parameter = parameter.substr(0, parameter.length - 1);
	return '{' + parameter + '}';
}
/**
 * 获取动态table：键、值，返回JSON
 * var GetTableData = GetTableDataJson("table的ID");
 */
function GetTableDataJson(tableId) {
	var item_Key_Value = "";
	var index = 1;
	var trjson = "";
	if ($(tableId + " tbody tr").length > 0) {
		$(tableId + " tbody tr").each(function () {
			var tdjson = "";
			$(this).find('td').find('input,select,textarea').each(function () {
				var pk_id = $(this).attr('id');
				// alert("pk_id："+ pk_id);
				var pk_value = "";
				if ($("#" + pk_id).attr('type') == "checkbox") {
					if ($("#" + pk_id).attr("checked")) {
						pk_value = "1";
					} else {
						pk_value = "0";
					}
				} else {
					pk_value = $("#" + pk_id).val();
				}
				//alert("pk_value："+ pk_value);

				var array = new Array();
				//array = pk_id.split("➩"); //字符分割
				array = pk_id.split("_"); //字符分割 XBM 
				tdjson += '"' + array[0] + '"' + ':' + '"' + $.trim(pk_value) + '",'
			})
			tdjson = tdjson.substr(0, tdjson.length - 1);
			trjson += '{' + tdjson + '},';
		});
	} else {
		$(tableId + " tr").each(function () {
			var tdjson = "";
			$(this).find('td').find('input,select,textarea').each(function () {
				var pk_id = $(this).attr('id');
				var pk_value = "";
				if ($("#" + pk_id).attr('type') == "checkbox") {
					if ($("#" + pk_id).attr("checked")) {
						pk_value = "1";
					} else {
						pk_value = "0";
					}
				} else {
					pk_value = $("#" + pk_id).val();
				}
				var array = new Array();
				//array = pk_id.split("➩"); //字符分割
				array = pk_id.split("_"); //字符分割 xbm
				alert("ok1："+array);
				tdjson += '"' + array[0] + '"' + ':' + '"' + $.trim(pk_value) + '",'
			})
			tdjson = tdjson.substr(0, tdjson.length - 1);
			trjson += '{' + tdjson + '},';
		});
	}
	trjson = trjson.substr(0, trjson.length - 1);
	if (trjson == '{}') {
		trjson = "";
	}
	return '[' + trjson + ']';//[{}]
}
/*
自动获取页面控件值
 */
function GetWebControls(element) {
	var reVal = "";
	$(element).find('input,select,textarea').each(function (r) {
		var id = $(this).attr('id');
		var value = $(this).val();

		//alert(id +":"+value);
		/*if (value != undefined && value != 'null') {
        	value = value.replace(/\r?\n/g,"");
        } */
		if(IsNullOrEmpty(value)) 
			value = value.replace(/\r?\n/g,"");


		var type = $(this).attr('type');
		switch (type) {
		case "checkbox":
			if ($(this).attr("checked")) {
				reVal += '"' + id + '"' + ':' + '"1",'
			} 
			else {
				reVal += '"' + id + '"' + ':' + '"0",'
			}
			break;
			/*default: XBM 2017 数据库写的默认值是：&nbsp;或：空
                if (value == "") {
                    value = "&nbsp;";
                }
                reVal += '"' + id + '"' + ':' + '"' + $.trim(value) + '",'
			 */
		default:
			reVal += '"' + id + '"' + ':' + '"' + value + '",'
			break;
		}
	});
//	reVal +='"'+ "xiugairen "+'"' + ':' + '"' + $.cookie("TIP") + '",'
	reVal = reVal.substr(0, reVal.length - 1);
	// alert(reVal.replace(/\r?\n/g,""));
	return jQuery.parseJSON('{' + reVal + '}');
}
/* 
请求Ajax 带返回值。 本地用
 */
function getAjax(url, postData, callBack)
{
	$.ajax({
		type: 'post',
		dataType: "text",
		url: url,
		data: postData,
		cache: false,
		async: false,
		success: function (data) {
			callBack(data);
		},
		error: function (data) {
			// alert("error:" + JSON.stringify(data));//将 JavaScript 值转换为 JavaScript 对象表示法 (Json) 字符串
			Loading(false);
		}
	});
}
/* 
请求Ajax 带返回值。 远程调用用
 */
function getAjaxJson(url, postData, callBack)
{
	//alert("OK:"+ url);///pite
	$.ajax({
		type: 'POST',
		dataType: "json", //json--返回json数据类型；xml--返回xml 
		url:  RootPath() + url,//
		data: postData,  
		processData : true, //contentType为xml时，值为false  
		cache: false,
		async: false,
		scriptCharset: 'utf-8',
		success: function (data) { 
			callBack(data);
		},
		error: function (data) {
			//把array 或者 object类型的数据转换为JSON格式
			//alert("error:" + JSON.stringify(data));//将 JavaScript 值转换为 JavaScript 对象表示法 (Json) 字符串。 
			Loading(false);
		}
	});
}


/**获取表格选择行
 **/
function GetJqGridRowIndx(jgrid) {
	return $(jgrid).jqGrid('getGridParam', 'selrow')
}

/**获取JqGrid表格列值
jgrid：ID;  code：列字段
 **/
function GetJqGridRowValue(jgrid, code) {
	var KeyValue = "";
	//获取选择多行的id获取选择多行的id
	var selectedRowIds = $(jgrid).jqGrid("getGridParam", "selarrrow");
	if (selectedRowIds != "") {
		var len = selectedRowIds.length;
		for (var i = 0; i < len ; i++) {
			var rowData = $(jgrid).jqGrid('getRowData', selectedRowIds[i]);
			KeyValue += rowData[code] + ",";
		}
		KeyValue = KeyValue.substr(0, KeyValue.length - 1);
	}
	else {
		//获取选择一行的id，如果你选择多行，那下面的id是最后选择的行的id：
		//var id= $(jgrid).jqGrid('getGridParam','selrow');
		//获取选中行数据（json）：
		//var rowData =  $(jgrid).jqGrid('getRowData', id);
		//取到选中行某一字段的值，其中name为colModel中定义的字段名
		//KeyValue = rowData[code];
		var rowData = $(jgrid).jqGrid('getRowData', $(jgrid).jqGrid('getGridParam', 'selrow'));
		KeyValue = rowData[code];
	}
	return KeyValue;
}
/**获取JqGrid表格列值
jgrid：ID，RowIndx:行ID,code：列字段
 **/
function GetJqGridValue(jgrid,RowIndx,code) {
	var rowData = $(jgrid).jqGrid('getRowData', RowIndx);
	return rowData[code];
}

/**grid表格扩展end**/

/********
接收地址栏参数
 **********/
function GetQuery(key) {

	var search = location.search.slice(1); //得到get方式提交的查询字符串
	var arr = search.split("&");
	for (var i = 0; i < arr.length; i++) {
		var ar = arr[i].split("=");
		if (ar[0] == key) {
			if (unescape(ar[1]) == 'undefined') {
				return "";
			} else {
				return unescape(ar[1]);
			}
		}
	}
	return "";
}

/*鼠标右击菜单begin========================================*/
var getOffset = {
		top: function (obj) {
			return obj.offsetTop + (obj.offsetParent ? arguments.callee(obj.offsetParent) : 0)
		},
		left: function (obj) {
			return obj.offsetLeft + (obj.offsetParent ? arguments.callee(obj.offsetParent) : 0)
		}
};


/*鼠标右击菜单end========================================*/
function IsDelData(id) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
		tipDialog('您没有选中任何项,请您选中后再操作。', 4, 'warning');
	}
	return isOK;
}

function IsQRCodeChecked(id) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
		tipDialog('请先输入唯一编号，然后再生成二维码。', 4, 'warning');
	} 
	return isOK;
}

function IsCheckedMenu(id) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
		tipDialog('您没有选取任何菜单项, 请选中后再操作。', 4, 'warning');
	} 
	return isOK;
}

function IsCheckedNull(id) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
	} 
	return isOK;
}

function IsCheckedValue(id,msg) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
		tipDialog(msg, 3, 'warning');
	} 
	return isOK;
}


function IsChecked(id) {
	var isOK = true;
	if (id == undefined || id == "" || id == 'null' || id == 'undefined') {
		isOK = false;
		tipDialog('您没有选中任何项, 请选中后再操作。', 4, 'warning');
	} else if (id.split(",").length > 1) {
		isOK = false;
		tipDialog('很抱歉, 一次只能选择一条记录。', 4, 'warning');
	}
	return isOK;
}

/*
验证是否为空
 */
function IsNullOrEmpty(str) {
	var isOK = true;
	if (str == undefined || str == "" || str == 'null') {
		isOK = false;
	}
	return isOK;
}
/**
文本框只允许输入数字
 **/
function IsNumber(obj) {
	$("#" + obj).bind("contextmenu", function () {
		return false;
	});
	$("#" + obj).css('ime-mode', 'disabled');
	$("#" + obj).keypress(function (e) {
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
	});
}
/**
只能输入数字和小数点
 **/
function IsMoney(obj) {
	$("#" + obj).bind("contextmenu", function () {
		return false;
	});
	$("#" + obj).css('ime-mode', 'disabled');
	$("#" + obj).bind("keydown", function (e) {
		var key = window.event ? e.keyCode : e.which;
		if (isFullStop(key)) {
			return $(this).val().indexOf('.') < 0;
		}
		return (isSpecialKey(key)) || ((isNumber(key) && !e.shiftKey));
	});
	function isNumber(key) {
		return key >= 48 && key <= 57
	}
	function isSpecialKey(key) {
		return key == 8 || key == 46 || (key >= 37 && key <= 40) || key == 35 || key == 36 || key == 9 || key == 13
	}
	function isFullStop(key) {
		return key == 190 || key == 110;
	}
}

//判断一个字符串是一个字符串数组中的一个元素。返回-1表示没找到，返回其他值表示找到的索引
function in_array(a, v) {
	if (!IsNullOrEmpty(a) || !IsNullOrEmpty(v) )	 //空为false
		return -1;  
	var i;
	for (i = 0; i < a.length; i++) {
		if (v === a[i]) {
			return i;
		}
	}
	return -1;
} 

/*
iframe同步连接
 */
function iframe_src(iframe, src) {
	Loading(true);
	$("#" + iframe).attr('src', RootPath() + src);
	$("#" + iframe).load(function () {
		Loading(false);
	});
}

/*绑定下拉框
ControlId:控件ID
Memo:默认显示
 */
function JsonBindDrop(ControlId, Memo, DataJson) {
	$(ControlId).html("");
	if (IsNullOrEmpty(Memo)) {
		$(ControlId).append("<option value=''>" + Memo + "</option>");
	}
	var DataJson = eval("(" + DataJson + ")");
	$.each(DataJson, function (i) {
		$(ControlId).append($("<option></option>").val(DataJson[i].Code).html(DataJson[i].FullName));
	});
}

/*
中间加载对话窗
 */
function Loading(bool, text) {
	var ajaxbg = top.$("#loading_background,#loading");
	if (!!text) {
		top.$("#loading").css("left", (top.$('body').width() - top.$("#loading").width()) / 2);
		top.$("#loading span").html(text);
	} else {
		top.$("#loading").css("left", (top.$('body').width() - top.$("#loading").width()) / 2);
		top.$("#loading span").html("正在拼了命为您加载…");
	}
	if (bool) {
		ajaxbg.show();
	} else {
		ajaxbg.hide();
	}
}


function Loadlayout() {
	if ($('.layout').length > 0) {
		$("#layout").splitter({
			type: "v",
			outline: true,
			minLeft: 150, sizeLeft: 200, maxLeft: 350,
			anchorToWindow: true,
			accessKey: "L"
		});
	}
}


function LoadrightMenu(element) {//element是：tab-nav li
	var oMenu = $('.rightMenu');
	$(document).click(function () {
		oMenu.hide();
	});
	$(document).mousedown(function (e) {//当鼠标指针移动到元素上方，并按下鼠标按键时，会发生 mousedown 事件
		if (3 == e.which) {
			oMenu.hide();
		}
	})
	var aUl = oMenu.find("ul");
	var aLi = oMenu.find("li");
	var showTimer = hideTimer = null;
	var i = 0;
	var maxWidth = maxHeight = 0;
	var aDoc = [document.documentElement.offsetWidth, document.documentElement.offsetHeight];
	oMenu.hide();
	for (i = 0; i < aLi.length; i++) {
		//为含有子菜单的li加上箭头
		aLi[i].getElementsByTagName("ul")[0] && (aLi[i].className = "sub");
		//鼠标移入
		aLi[i].onmouseover = function () {
			var oThis = this;
			var oUl = oThis.getElementsByTagName("ul");
			//鼠标移入样式
			oThis.className += " active";
			//显示子菜单
			if (oUl[0]) {
				clearTimeout(hideTimer);
				showTimer = setTimeout(function () {
					for (i = 0; i < oThis.parentNode.children.length; i++) {
						oThis.parentNode.children[i].getElementsByTagName("ul")[0] &&
						(oThis.parentNode.children[i].getElementsByTagName("ul")[0].style.display = "none");
					}
					oUl[0].style.display = "block";
					oUl[0].style.top = oThis.offsetTop + "px";
					oUl[0].style.left = oThis.offsetWidth + "px";

					//最大显示范围					
					maxWidth = aDoc[0] - oUl[0].offsetWidth;
					maxHeight = aDoc[1] - oUl[0].offsetHeight;

					//防止溢出
					maxWidth < getOffset.left(oUl[0]) && (oUl[0].style.left = -oUl[0].clientWidth + "px");
					maxHeight < getOffset.top(oUl[0]) && (oUl[0].style.top = -oUl[0].clientHeight + oThis.offsetTop + oThis.clientHeight + "px")
				}, 300);
			}
		};
		//鼠标移出	
		aLi[i].onmouseout = function () {
			var oThis = this;
			var oUl = oThis.getElementsByTagName("ul");
			//鼠标移出样式
			oThis.className = oThis.className.replace(/\s?active/, "");
			clearTimeout(showTimer);
			hideTimer = setTimeout(function () {
				for (i = 0; i < oThis.parentNode.children.length; i++) {
					oThis.parentNode.children[i].getElementsByTagName("ul")[0] &&
					(oThis.parentNode.children[i].getElementsByTagName("ul")[0].style.display = "none");
				}
			}, 300);
		};
	}
	/*  $(document).ready(function(){}),即绑定文档加载完毕后事件
        $(document).bind('contextmenu',function(){})是绑定右键菜单事件*/
	//自定义右键菜单
	$(element).bind("contextmenu", function () {
		//window.event适用于ie和google；arguments.callee.caller.arguments[0]适用于火狐和google
		//var event = event || window.event; //原来的语句
		var event = arguments.callee.caller.arguments[0]|| window.event; 
		oMenu.show();
		oMenu.css('top', event.clientY + "px");
		oMenu.css('left', event.clientX + "px");
		//最大显示范围
		maxWidth = aDoc[0] - oMenu.width();
		maxHeight = aDoc[1] - oMenu.height();
		//防止菜单溢出
		if (oMenu.offset().top > maxHeight) {
			oMenu.css('top', maxHeight + "px");
		}
		if (oMenu.offset().left > maxWidth) {
			oMenu.css('left', maxWidth + "px");
		}
		return false;
	}).bind("click", function () {
		oMenu.hide();
	});
}

/*
弹出对话框
 */
function MessageDialog(_html, _id, _title, _width, _height, _callBack) {
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		max: false,
		min: false,
		title: _title,
		content: _html,
		ok: function () {
			var data = eval("(" + top.GetParameterJson(_id) + ")");;
			_callBack(data);
			return false;
		},
		cancel: true
	});
}
/*
弹出打印网页
 */
function PrintDialog(url, _id, _title, _width, _height, _callBack) {
	Loading(true);
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		max: false,
		lock: true,
		title: _title,
		content: 'url:' + RootPath() + url,
		button: [
			{
				name: '打 印',
				callback: function () {
					_callBack(_id);
					return false;
				}
			},
			{
				name: '取 消'
			}
			]
	});
}

/*打开网页 window.open
/*url:          表示请求路径
/*windowname:   定义页名称
/*width:        宽度
/*height:       高度
---------------------------------------------------*/
function OpenWindow(url, title, w, h) {
	var width = w;
	var height = h;
	var left = ($(window).width() - width) / 2;
	var top = ($(window).height() - height) / 2;
	window.open(RootPath() + url, title, 'height=' + height + ', width=' + width + ', top=' + top + ', left=' + left + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no, titlebar=yes, alwaysRaised=yes');
}
/*
弹出对话框（带：确认按钮、取消按钮）
$=jQuery
 */
function openDialog(url, _id, _title, _width, _height, callBack) {

	Loading(true);
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		max: false,
		lock: true,
		title: _title,
		resize: false,
		extendDrag: true,
		content: 'url:' + RootPath() + url,
		ok: function () {
			callBack(_id);
			return false;
		},
		cancel: true
	});
}

//关闭除当前之外的tab
function othercloseTab() {
	var tabs_container = top.$("#tabs_container");
	var id = tabs_container.find('.selected').attr('id').substr(5);
	top.$(".tab-nav").find("[win_close=true]").each(function () {
		if ($(this).attr('id').substr(5) != id) {
			RemoveDiv($(this).attr('id').substr(5))
		}
	});
}
/*弹出对话框begin========================================*/
/*
弹出查询
 */
function QueryDialog(url, _id, _title, _width, _height, _callBack) {
	Loading(true);
	top.$.dialog({
		id: _id,
		width: _width,
		height: _height,
		max: false,
		lock: true,
		title: _title,
		content: 'url:' + RootPath() + url,
		button: [
			{
				name: '查 询',
				callback: function () {
					_callBack(_id);
					return false;
				}
			},
			{
				name: '取 消'
			}
			]
	});
}

function QueryConChecked(value,second) {
	var isOK = true;
	if (value == undefined || value == "" || value == 'null' || value == 'undefined') {
		isOK = false;
		tipDialog('请输入查询条件!', 2, 'warning');
	} 
	return isOK;
}

function QueryConCheckedV2(value,second) {
	var isOK = true;
	if (value == undefined || value == "" || value == 'null' || value == 'undefined') {
		isOK = false;
		tipDialog('请输入车次!', 2, 'warning');
	} 
	return isOK;
}
function QueryConCheckedV3(value,second) {
	var isOK = true;
	if (value == undefined || value == "" || value == 'null' || value == 'undefined') {
		isOK = false;
	} 
	return isOK;
}

//pages/items/LoadButton"
function PartialButton(url) 
{
	var JsonData = "";
	var toolbar_html = "";
	var toolbar_Children_Count = 0;
	AjaxJson(url, {}, function (Data) {
		JsonData = Data;
		$.each(Data, function (i) {
			if (Data[i].ParentId == '0' && Data[i].Category == '1') {
				var toolbar_Children_List = Toolbar_Children(Data[i].ButtonId, Data)
				if (toolbar_Children_Count > 0) {//处理下拉菜单
					toolbar_html += "<a id=\"" + Data[i].Code + "\" ids=\"" + Data[i].ButtonId + "\" class=\"tools_btn dropdown\">";
					toolbar_html += "<div style=\"float: left;\"><div class=\"icon\"><img src=\"../scripts/images/Icon16/" + Data[i].Icon + "\" /></div><div class=\"text\">" + Data[i].FullName + "</div></div>";
					toolbar_html += "<div class=\"dropdown-icon\"><img src=\"../scripts/images/dropdown-icon.png\" /></div>";
					toolbar_html += "<div class=\"dropdown-data\"><i></i><span></span>";
					toolbar_html += "    <ul>";
					toolbar_html += toolbar_Children_List;
					toolbar_html += "    </ul>";
					toolbar_html += "</div>";
					toolbar_html += "</a>";
				} else {
					var title = "title=\"" + Data[i].Remark + "\"";
					if (Data[i].Remark == "") { title = ""; }
					toolbar_html += "<a id=\"" + Data[i].Code + "\" ids=\"" + Data[i].ButtonId + "\" " + title + " onclick=\"" + Data[i].JsEvent + "\" class=\"tools_btn\"><span><b style=\"background: url('../scripts/images/Icon16/" + Data[i].Icon + "') 50% 4px no-repeat;\">" + Data[i].FullName + "</b></span></a>";
				}
				if (Data[i].Split == '1') {
					toolbar_html += "<div class=\"tools_separator\"></div>";
				}
			}
		});
	});
	$('.tools_bar .PartialButton').html(toolbar_html);
	function Toolbar_Children(ButtonId, Data) {
		var _toolbar_Children = "";
		toolbar_Children_Count = 0;
		$.each(Data, function (i) {
			if (Data[i].ParentId == ButtonId) {
				var title = "title=\"" + Data[i].Remark + "\"";
				if (Data[i].Remark == "") { title = ""; }
				_toolbar_Children += "<li id=\"" + Data[i].Code + "\" ids=\"" + Data[i].ButtonId + "\" " + title + " onclick=\"" + Data[i].JsEvent + "\"><img src=\"../scripts/images/Icon16/" + Data[i].Icon + "\" />" + Data[i].FullName + "</li>";
				toolbar_Children_Count++;
			}
		});
		return _toolbar_Children;
	}
	$(".tools_btn").hover(function () {
		$(this).addClass("tools_btn_hover");
	}, function () {
		$(this).removeClass("tools_btn_hover");
	});
	$(".tools_bar .dropdown").hover(function () {
		var left = $(this).offset().left - ($(this).find('.dropdown-data').width() / 2) + ($(this).width() / 2 + 9);
		$(this).find('.dropdown-data').show().css('top', ($(this).offset().top + 50)).css('left', left);
		$(this).find('.dropdown-icon').addClass('dropdown-icon-hover');
		$(this).addClass('dropdown-selected');
	}, function () {
		if (!$(this).hasClass("_click")) {
			$(this).removeClass('dropdown-selected');
			$(this).find('.dropdown-data').hide();
			$(this).find('.dropdown-icon').removeClass('dropdown-icon-hover');
		}
	});
	/*$('.tools_bar .dropdown').toggle(function () {//jquery已不支持此函数 XBM
        $(this).addClass('_click');
        var left = $(this).offset().left - ($(this).find('.dropdown-data').width() / 2) + ($(this).width() / 2 + 9);
        $(this).find('.dropdown-data').show().css('top', ($(this).offset().top + 50)).css('left', left);
        $(this).find('.dropdown-icon').addClass('dropdown-icon-hover');
        $(this).addClass('dropdown-selected');
    }, function () {
        $(this).removeClass('dropdown-selected');
        $(this).find('.dropdown-data').hide();
        $(this).find('.dropdown-icon').removeClass('dropdown-icon-hover');
        $('.dropdown').removeClass('_click');
    });*/
	$(".dropdown-data li").click(function () {
		$('.dropdown').removeClass('dropdown-selected');
		$('.dropdown').find('.dropdown-data').hide();
		$('.dropdown').find('.dropdown-icon').removeClass('dropdown-icon-hover');
		$('.dropdown').removeClass('_click');
	});

	//右击菜单
	var right_toolbar_Count = 0;
	var right_toolbar_html = "";
	right_toolbar_html += '<div class="rightMenu"><ul>';

	$.each(JsonData, function (i) {
		if (JsonData[i].ParentId == '0' && JsonData[i].Category == '2') {
			var title = "title=\"" + JsonData[i].Remark + "\"";
			if (JsonData[i].Remark == "") { title = ""; }
			var righttoolbar_Children_List = right_toolbar_Children(JsonData[i].ButtonId, JsonData)
			if (right_toolbar_Count > 0) {
				right_toolbar_html += "<li id=\"right_" + JsonData[i].Code + "\" ids=\"" + JsonData[i].ButtonId + "\" " + title + " onclick=\"" + JsonData[i].JsEvent + "\" ><img src=\"../scripts/images/Icon16/" + JsonData[i].Icon + "\" />" + JsonData[i].FullName + righttoolbar_Children_List + "</li>";
			} else {
				right_toolbar_html += "<li id=\"right_" + JsonData[i].Code + "\"  ids=\"" + JsonData[i].ButtonId + "\" " + title + " onclick=\"" + JsonData[i].JsEvent + "\" ><img src=\"../scripts/images/Icon16/" + JsonData[i].Icon + "\" />" + JsonData[i].FullName + "</li>";
			}
			if (JsonData[i].Split == '1') {
				right_toolbar_html += "<div class=\"m-split\"></div>";
			}
		}
	});
	right_toolbar_html += '</ul></div>';
	function right_toolbar_Children(ButtonId, JsonData) {
		var _right_toolbar = "<ul>";
		right_toolbar_Count = 0;
		$.each(JsonData, function (i) {
			if (JsonData[i].ParentId == ButtonId) {
				var title = "title=\"" + JsonData[i].Remark + "\"";
				if (JsonData[i].Remark == "") { title = ""; }
				_right_toolbar += "<li id=\"right_" + JsonData[i].Code + "\"  ids=\"" + JsonData[i].ButtonId + "\" " + title + " onclick=\"" + JsonData[i].JsEvent + "\" ><img src=\"../scripts/images/Icon16/" + JsonData[i].Icon + "\" />" + JsonData[i].FullName + "</li>";
				if (JsonData[i].Split == '1') {
					_right_toolbar += "<div class=\"m-split\"></div>";
				}
				right_toolbar_Count++;
			}
		});
		return _right_toolbar + "</ul>";
	}
	$("body").append(right_toolbar_html);
	if ($('.rightMenu').find('li').length > 0) {
		LoadrightMenu("#grid_List");
	} else {
		$('.rightMenu').remove();
	}
}

//关闭事件 : xbm目前暂不处理 ModuleId
function RemoveDiv(obj) 
{
	Loading(false);
	var tabs_container = top.$("#tabs_container");
	var ContentPannel = top.$("#ContentPannel");
	/*xbm var ModuleId = tabs_container.find('.selected').attr('id').substr(5);//原来ID
    var ModuleName = tabs_container.find('.selected').find('span').attr('title');//原来名称
    SetLeave(ModuleId, ModuleName);*/
	tabs_container.find("#tabs_" + obj).remove();
	ContentPannel.find("#tabs_iframe_" + obj).remove();
	var tablist = tabs_container.find('li');
	var pannellist = ContentPannel.find('iframe');
	if (tablist.length > 0) {
		tablist[tablist.length - 1].className = 'selected';
		pannellist[tablist.length - 1].style.display = 'block';
		/*xbm var id = tabs_container.find('.selected').attr('id').substr(5);
        VisitorModule(id);*/
	}
}
/*
该方法参数为 false 或者省略参数,重新加载当前文档,即刷新当前页面
 */
function Replace() 
{
	
	location.reload();// 参数 默认为 false，从客户端缓存里取当前页。true, 则以 GET 方式，从服务端取最新的页面, 相当于客户端点击 F5("刷新") 
	//window.location.href = window.location.href.replace(/#.*$/, '');
	return false;
}

//js获取网站根路径(站点及虚拟目录,不含项目目录)
function RootPath() 
{
	//获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
	var strFullPath = window.document.location.href;
	//获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
	var strPath = window.document.location.pathname;
	var pos = strFullPath.indexOf(strPath);
	//获取主机地址，如： http://localhost:8083
	var prePath = strFullPath.substring(0, pos);
	//获取带"/"的项目名，如：/uimcardprj
	var postPath = strPath.substring(0, strPath.substr(1).indexOf('/') + 1);
	return (prePath + postPath);
}

//离开模块
function SetLeave(ModuleId, ModuleName) {
	//  getAjax("/Home/SetLeave", { ModuleId: ModuleId, ModuleName: ModuleName }, function (data) { })
}

/*公共导出Excel，将表格数据取出来。在导出Excel
obj:表格对象ID
fileName：导出Excel文件名
 */
function SetDeriveExcel(obj, fileName) {
	var columnModel = $(obj).jqGrid('getGridParam', 'colModel');
	var dataModel = $(obj).jqGrid("getRowData");
	var footerData = $(obj).jqGrid("footerData");
	var JsonData = {
			JsonColumn: JSON.stringify(columnModel),
			JsonData: JSON.stringify(dataModel),
			JsonFooter: JSON.stringify(footerData),
			FileName: fileName
	}
	alert(JsonData);
	//getAjax("/Utility/SetDeriveExcel", JsonData, function (data) { })
}

/*
自动给控件赋值
 */
function SetWebControls(data) {
	try {
		for (var key in data) {
			var id = $('#' + key);
			if (id.attr('id')) {
				var value = $.trim(data[key]).replace("&nbsp;", "").replace(/</g, "<").replace(/>/, ">");
				var type = id.attr('type');
				switch (type) {
				case "checkbox":
					if (value == 1) {
						id.attr("checked", 'checked');
					} else {
						id.removeAttr("checked");
					}
					break;
				default:
					id.val(value);
				break;
				}
			}
		}
	} catch (e) {
		alert("Error:"+e)
	}
}

function standTabchange(e, id) {
	$(e).parent().find('div').removeClass("standtabactived");
	$(e).addClass("standtabactived");
	$(e).parent().next().children('div').hide();
	$("#" + id).show();
}

/*
自动给控件赋值、对Lable
 */
function SetWebLable(data) {
	for (var key in data) {
		var id = $('#' + key);
		if (id.attr('id')) {
			var value = $.trim(data[key]).replace("&nbsp;", "");
			id.text(value);
		}
	}
}


//保留两位小数    
//功能：将浮点数四舍五入，取小数点后2位   
function ToDecimal(x) {
	var f = parseFloat(x);
	if (isNaN(f)) {
		return 0;
	}
	f = Math.round(x * 100) / 100;
	return f;
}

//去空格  
function trim(str){       
	return str.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,'');       
}

/***
 * 自动关闭弹出内容提示
timeOut : 4000,				//提示层显示的时间
msg : "恭喜你!你已成功操作此插件，谢谢使用！",			//显示的消息
type : "success"//提示类型（1、success 2、error 3、warning）
 ***/
function TipMsg(msg, timeOut, type) {
	$(".tip_container").remove();
	var bid = parseInt(Math.random() * 100000);
	$("body").prepend('<div id="tip_container' + bid + '" class="container tip_container"><div id="tip' + bid + '" class="mtip"><span id="tsc' + bid + '"></span></div></div>');
	var $this = $(this);
	var $tip_container = $("#tip_container" + bid);
	var $tip = $("#tip" + bid);
	var $tipSpan = $("#tsc" + bid);
	//先清楚定时器
	clearTimeout(window.timer);
	//主体元素绑定事件
	$tip.attr("class", type).addClass("mtip");
	$tipSpan.html(msg);
	$tip_container.slideDown(300);
	//提示层隐藏定时器
	window.timer = setTimeout(function () {
		$tip_container.slideUp(300);
		$(".tip_container").remove();
	}, timeOut);
	//鼠标移到提示层时清除定时器
	$tip_container.live("mouseover", function () {
		clearTimeout(window.timer);
	});
	//鼠标移出提示层时启动定时器
	$tip_container.live("mouseout", function () {
		window.timer = setTimeout(function () {
			$tip_container.slideUp(300);
			$(".tip_container").remove();
		}, timeOut);
	});
	$("#tip_container" + bid).css("left", ($(window).width() - $("#tip_container" + bid).width()) / 2);
}


/*
获取动态tab标签当前iframeID
 */
function tabiframeId() {
	var tabs_container = top.$("#tabs_container");
	return "tabs_iframe_" + tabs_container.find('.selected').attr('id').substr(5);
}


//刷新当前tab
function ThisRefreshTab() {
	var tab_iframe = tabiframeId();
	var url = document.getElementById(tab_iframe).src;
	document.getElementById(tab_iframe).src=url;

}

//关闭当前tab
function ThisCloseTab() {
	var tabs_container = top.$("#tabs_container");
	top.RemoveDiv(tabs_container.find('.selected').attr('id').substr(5));
}

//Tab标签切换
function Tabchange(id) {
	$('.ScrollBar').find('.tabPanel').hide();
	$('.ScrollBar').find("#" + id).show();
	$(".tab_list_top div").removeClass("actived");
	$(".tab_list_top").find("#Tab" + id).addClass("actived"); //添加选中样式  
}

//底部标签切换
function Tabbuttomchange(id) {
	$('.ScrollBar').find('.tabPanel').hide();
	$('.ScrollBar').find("#" + id).show();
	$(".bottom div").removeClass("actived");
	$(".bottom").find("#Tab" + id).addClass("actived"); //添加选中样式  
}

function thisTabchange(e, id) {
	$(e).parent().find('div').removeClass("actived");
	$(e).addClass("actived");
	$('.tabPanel').hide();
	$("#" + id).show();
}

/** 
短暂提示
msg: 显示消息
time：停留时间
type：类型 >1：成功，<1：失败，其他：警告
 **/
function tipDialog(msg, time, type) {
	var msg = "<div class='ui_alert_tip'>" + msg + "</div>"
	if (type >= 1) {
		top.$.dialog.tips(msg, time, 'succ.png');
	} else if (type == -1) {
		top.$.dialog.tips(msg, time, 'fail.png');
	} else if (type == 0) {
		top.$.dialog.tips(msg, time, 'fail.png');
	} else {
		top.$.dialog.tips(msg, time, 'i.png');
	}
}

function tip(key, msg, time, side)
{
	var id = '#' + key;
	$(id).tips({
		side :side,
		msg : msg,
		bg : '#FF5080',
		time : time
	});
	$(id).focus();
}


/**
短暂提示
msg: 显示消息
time：停留时间
type：类型 >1：成功，<1：失败，其他：警告
 **/
function tipDialog1(msg) 
{
	var d = dialog({content: msg}).show();
	setTimeout(function(){d.close().remove();},2000);//2秒
}

function tipDialog2(_title,msg) 
{
	dialog({title:_title, content: msg}).show();
}

/**获取表格选中某行的所有值*/
function GetJqGridSelRowValue(jgrid)
{
	var json = {};
	
	//获取选择多行的id获取选择多行的id
    var selectedRowId = $(jgrid).jqGrid("getGridParam", "selrow");
    if (selectedRowId == "") return json;
    
    //如果想获取选择的行的数据。rowData是一个对象
	var rowData = $(jgrid).jqGrid('getRowData',selectedRowId);
    //获取title属性的值列名,label
	//var colNames = $(jgrid).jqGrid('getGridParam','colNames')
	//jqgrid获取Column属性.columnArray[i].name获取name属性的值，也可以通过columnArray[i].index获取index属性的值。
	var columnArray = $(jgrid).jqGrid('getGridParam','colModel')
 	if (columnArray != "") {
        var len = columnArray.length;
        var colName = "";
        var KeyValue = "";
        for (var i = 1; i < len ; i++) {//JS动态生成JSON对象
            colName = columnArray[i].name;
            KeyValue = rowData[colName];
            json[colName] = KeyValue;
        }
    }
	return json;
}

/*
href跳转页面
 */
function Urlhref(url) {
	location.href = url;
	return false;
}

//访问模块，写入系统菜单Id
function VisitorModule(ModuleId, ModuleName) {
	// top.$("#ModuleId").val(ModuleId);
	// getAjax("/Home/SetModuleId", { ModuleId: ModuleId, ModuleName: ModuleName }, function (data) { })
}

function genKongkuahaoByDuntaiHao(qiaohao, kongkuashu, duntaihao){
	var optionstring = "";  
	if(isInteger(kongkuashu)){//是一整数 。第一个为000.桥中间的叫墩D，两端的叫台T。kongkuashu.length
		if (kongkuashu != 0){
			for (var i = 0; i <= kongkuashu; i++) {
				var preZero = "0";
				if(i < 10)  preZero = "00";
				else if(i < 100) preZero = "0";
				else if(i > 100) preZero = "";
				var code = "";  
				if(i == 0) code = qiaohao + "-T000";
				else if(i == kongkuashu) code = qiaohao + "-T" + preZero + kongkuashu;
				else code = qiaohao + "-D" + preZero + i;

				if(code == duntaihao) {
					optionstring = i;
					break;
				}
			}
		}
	}
	return optionstring;  
}
//主要供bridge_duntai_add.html使用产生墩台号. 全路具有唯一性。
//对所有的孔的的左墩台均自动生成两个墩号，即qiaohao + "-D" + preZero + i; 和  qiaohao + "-D" + preZero + i+ "D";
function genDuntaiHao(qiaohao, kongkuashu){
	var optionstring = "";  
	if(isInteger(kongkuashu)){//是一整数 。第一个为000.桥中间的叫墩D，两端的叫台T。kongkuashu.length
		if (kongkuashu != 0){
			for (var i = 0; i <= kongkuashu; i++) {
				var preZero = "0";
				if(i < 10)
					preZero = "00";
				else if(i < 100)
					preZero = "0";
				else if(i > 100)
					preZero = "";
				var code = "";  
				if(i == 0)	
					code = qiaohao + "-T000";
				else if(i == kongkuashu)
					code = qiaohao + "-T" + preZero + kongkuashu;
				else
					code = qiaohao + "-D" + preZero + i;
				optionstring += "<option value=\"" + code + "\" >" + code + "</option>";

				//if(code.indexOf("-D") > 0){
				//	code = qiaohao + "-D" + preZero + i+ "D";
					
				//	optionstring += "<option value=\"" + code + "\" >" + code + "</option>";
				//}
			}
		}
	}
	return optionstring;  
}
//产生特定孔跨号的墩台号. Kongkuahao从1开始编码 
function genDuntaiHao4Kongkuahao(qiaohao,kongkuashu, Kongkuahao){
	var optionstring = "";  
	//是一整数 。第一个为000.桥中间的叫墩D，两端的叫台T。kongkuashu.length
	if(isInteger(kongkuashu) &&  isInteger(Kongkuahao) && (Kongkuahao != 0) ){
		var preZero = "0";
		if(Kongkuahao < 10)  preZero = "00";
		else if(Kongkuahao < 100) preZero = "0";
		else if(Kongkuahao > 100) preZero = "";

		var leftCode = "";
		var rightCode = "";
		if( Kongkuahao == 1){ //第1孔
			leftCode = qiaohao + "-T000";//左墩台号
			if(Kongkuahao == kongkuashu)//右墩台号
				rightCode = qiaohao + "-T001";
			else
				rightCode = qiaohao + "-D" + preZero + Kongkuahao;
		}
		else if(Kongkuahao == kongkuashu){ //最后1孔
			leftCode = qiaohao + "-D" + preZero + (kongkuashu-1);//左墩台号
			rightCode = qiaohao + "-T" + preZero + kongkuashu;
		}
		else { //中间的孔
			leftCode = qiaohao + "-D" + preZero + (Kongkuahao-1);//左墩台号
			rightCode = qiaohao + "-D" + preZero + Kongkuahao;
		}
		optionstring += "<option value=\"" + leftCode + "\" >" + leftCode + "</option>";
		optionstring += "<option value=\"" + rightCode + "\" >" + rightCode + "</option>";
	}
	return optionstring;  
}
//给定孔跨号的左右墩台的编号
function genTwoDuntaiHao(qiaohao, kongkuashu,kongkuahao){
	var optionstring = "";  
	if(isInteger(kongkuashu)){
		if (kongkuashu != 0){
			for (var i = kongkuahao - 1 ; i <= kongkuahao; i++) {
				var preZero = "0";
				if(i < 10)
					preZero = "00";
				else if(i < 100)
					preZero = "0";
				else if(i > 100)
					preZero = "";
				var code = "";  
				if(i == 0)	
					code = qiaohao + "-T000";
				else if(i == kongkuashu)
					code = qiaohao + "-T" + preZero + kongkuashu;
				else
					code = qiaohao + "-D" + preZero + i;
				optionstring += "<option value=\"" + code + "\" >" + code + "</option>";
			}
		}
	}
	return optionstring;  
}
//给定孔跨号的左墩台的编号
function genOneDuntaiHao(qiaohao,kongkuashu,kongkuahao){
	var optionstring = "";  
	if(isInteger(kongkuashu)){//是一整数 。第一个为000.桥中间的叫墩D，两端的叫台T。kongkuashu.length
		if (kongkuashu != 0)
			for (var i = 0; i <= kongkuashu; i++) {
				var preZero = "0";
				if(i < 10)
					preZero = "00";
				else if(i < 100)
					preZero = "0";
				else if(i > 100)
					preZero = "";
				if(i == 0)	
					optionstring = qiaohao + "-T000";
				else if(i == kongkuashu)
					optionstring = qiaohao + "-T" + preZero + kongkuashu;
				else
					optionstring = qiaohao + "-D" + preZero + i;
				if(i == (kongkuahao - 1))
					break;
			}
	}
	return optionstring;  
}
//给定孔跨号的右墩台的编号
function genSecondDuntaiHao(qiaohao,kongkuashu,kongkuahao){
	var optionstring = "";  
	if(isInteger(kongkuashu)){//是一整数 。第一个为000.桥中间的叫墩D，两端的叫台T。kongkuashu.length
		if (kongkuashu != 0)
			for (var i = 0; i <= kongkuashu; i++) {
				var preZero = "0";
				if(i < 10)
					preZero = "00";
				else if(i < 100)
					preZero = "0";
				else if(i > 100)
					preZero = "";
				if(i == 0)	
					optionstring = qiaohao + "-T000";
				else if(i == kongkuashu)
					optionstring = qiaohao + "-T" + preZero + kongkuashu;
				else
					optionstring = qiaohao + "-D" + preZero + i;
				if(i == (kongkuahao))
					break;
			}
	}
	return optionstring;  
}

function genKongkuahao3(kongkuashu){
	var optionstring = "";
	if(isInteger(kongkuashu)){//是一整数 
		if (kongkuashu != 0)
			for (var i = 1; i <= kongkuashu; i++)
				optionstring += "<option value=\"" + i + "\" >" + i + "</option>";
	}
	return optionstring;  
}

function genKongkuahao(kongkuashu){
	var optionstring = "";
	//optionstring += "<option value=\"" + '-1' + "\" >" + '请选取孔跨号(不选将新增所有孔跨数的信息)！' + "</option>";
	if(isInteger(kongkuashu)){//是一整数 
		if (kongkuashu != 0)
			for (var i = 1; i <= kongkuashu; i++)
				optionstring += "<option value=\"" + i + "\" >" + i + "</option>";
	}
	return optionstring;  
}
