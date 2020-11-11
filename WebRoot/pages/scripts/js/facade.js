$(function () {
    $(window).load(function () {
        window.setTimeout(function () {
            $('#ajax-loader').fadeOut();
        }, 200);
    });
});
//图标闪乐
function IconSong(id) {
    var $obj = $("#" + id);
    if (!$obj.hasClass(id + '_ok')) {
        $obj.addClass(id + '_ok');
        $obj.hide();
    } else {
        $obj.removeClass(id + '_ok');
        $obj.show();
    }
}

//样式 自动显示下拉菜单
function readyIndex() {
    $(".main_menu li div").click(function () {
        $(".main_menu li div").removeClass('main_menu leftselected');
        $(this).addClass('main_menu leftselected');
    }).hover(function () {
        $(this).addClass("hoverleftselected");
    }, function () {
        $(this).removeClass("hoverleftselected");
    });
    //点击TOP按钮显示标签
    $("#topnav .droppopup a").hover(function () {
        $("#topnav .droppopup a").removeClass('onnav');
        $(this).addClass('onnav');
        var Y = $(this).attr("offsetLeft");
        $(this).find('.popup').show().css('top', ($(this).offset().top + 71)).css('left', $(this).offset().left - ($(this).find('.popup').width() / 2 - 36));
    }, function () {
        $("#topnav .droppopup a").removeClass('onnav');
        $(this).find('.popup').hide();
    });
    $(".popup li").click(function () {
        $('.popup').hide();
    })
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
    var currentDate = year + "年" + month + "月" + date + "日";
    $("#CurrentDate").text(currentDate);
    var timer = setTimeout("ServerCurrentTime()", 1000);
}

//页面关闭事件
function PageClose() 
{
    var n = window.event.screenX - window.screenLeft;
    var b = n > document.documentElement.scrollWidth - 20;
    if (b && window.event.clientY < 0 || window.event.altKey) {
        window.location.href = "/SamSim/index.html";
    }
}

//退出系统
function IndexOut() 
{
	$.dialog.confirm('确定要退出系统吗？', function ()	{
	     getAjaxJson("/logout.do", "", function (data) {
		    if("logout" == data.result){
				   window.location.href = "http://www.railwills.xyz:8089/wwwroot/new4/logout.asp";
			}
		 });
	}, function () {
		//art.dialog.tips('执行取消操作');
	});
}


//关于我们
function About() 
{
	Dialog("/pages/facade/about.html", "AboutUs", "编组站行车组织虚拟仿真实训系统V1.0", 600, 350);
}

//技术支持
function Support() 
{
   aboutDialog("技术支持","<p>暂未开发</p>", 0);
}

//修改密码
function ResetPassword()
{
  var url = "/pages/facade/password.html";
  Dialog(url, "ResetPassword", "修改密码", 300, 140);//url, _id, _title, _width, _height
}

//个人信息
function PersonInfo() {
  var url = "/pages/facade/personInfo.html";
  Dialog(url, "PersonInfo", "个人信息",300, 290);//url, _id, _title, _width, _height
}

function SysDB() 
{
	getAjaxJson("/sysTimageinfo.do", "", function (data) { });
}

//个人中心550, 240
function PersonCenter() {
	var url = "/pages/facade/personCenter.html";
	Dialog(url, "PersonCenter", "个人中心", 650, 270);//url, _id, _title, _width, _height
}

//快捷方式列表
function ShortcutsList() 
{
    $("#Shortcuts").html('');
    AjaxJson("/pages/facade/shortcutsList.json", {}, function (DataJson) {
        $.each(DataJson, function (i) {
        	$("#Shortcuts").append("<li onclick=\"AddTabMenu('" + DataJson[i].ModuleId + "', '" + DataJson[i].Location + "', '" + DataJson[i].FullName + "',  '" + DataJson[i].Icon + "','true');\"><img src=\"../scripts/images/Icon16/" + DataJson[i].Icon + "\" />" + DataJson[i].FullName + "</li>");
        });
    });
    $(".popup li").click(function () {
        $('.popup').hide();
    })
}

//快捷方式设置
function Shortcuts() {
    var url = "/pages/facade/Shortcuts";
    openDialog(url, "Shortcut", "快捷方式设置", 500, 300, function (iframe) {
        top.frames[iframe].AcceptClick()
    });
}

//个性化皮肤设置
function SkinIndex() {
    Dialog("/pages/mainframe/mainframe_skinindex.jsp", "SkinIndex", "个性化设置", 580, 400);
}

