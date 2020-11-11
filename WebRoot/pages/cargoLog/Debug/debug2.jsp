<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<body>
<script type="text/javascript">
$('.memu .menu_list').parent('li').hover(function(){
	4102	$(this).find('.menu_list').slideDown(100);
	 $(this).find('>a').addClass('dq');
		},function(){
	 box=$(this).find('.menu_list')
	 s_1=$(this).find('>a')
	 t=setTimeout("box.hide();s_1.removeClass('dq');",300);
	 $(this).find('.menu_list').hover(function(){clearTimeout(t);s_1.addClass('dq');},function(){$(this).hide();s_1.removeClass('dq');})
		})
</script>
</body>
</html>