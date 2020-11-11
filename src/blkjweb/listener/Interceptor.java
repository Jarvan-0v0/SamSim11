package blkjweb.listener;

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;  
import blkjweb.utils.Const;
  
public class Interceptor extends HandlerInterceptorAdapter
{  
	//在控制器方法调用前执行
    //返回值为是否中断，true,表示继续执行（下一个拦截器或处理器）
    //false则会中断后续的所有操作，所以我们需要使用response来响应请求
    @Override    
    public boolean preHandle(HttpServletRequest request,    
            HttpServletResponse response, Object handler) throws Exception 
    {  
    	  //1、请求到登录页面 放行
      	  // 获得请求路径的uri 方法一
    	  String uri = request.getRequestURI();
    	  // 进入登录页面，判断session中是否有key，有的话重定向到首页，否则进入登录界面
    	  if(uri.contains("login") || uri.contains("validateCode")) {
   	          return true;//继续登陆请求
    	  }
    	//限制仅允许从本机访问 if (request.getLocalAddr().equals("127.0.0.1") || request.getLocalAddr().equals("0.0.0.0"))
    	//1、请求到登录页面 放行  方法二  
        /*if ( request.getServletPath().equalsIgnoreCase("/validateCode.do") 
        	 || request.getServletPath().equalsIgnoreCase("/login.do") 
            // || request.getServletPath().equalsIgnoreCase("/sendPWByEmail.do") 
            // || request.getServletPath().equalsIgnoreCase("/register.do")   
         ) 
        {//继续登陆请求
            return true; 
        }*/ 
        String username =  (String)request.getSession().getAttribute(Const.SESSION_USERNAME);
        //判断如果没有取到用户信息，就跳转到登陆页面，提示用户进行登陆 
        if(username == null || "".equals(username)){
         	 // 最后的情况就是进入登录页面
         	//response.sendRedirect(request.getContextPath() + "/login");
          	return false;  
        }
        else{//继续用户的操作
        	return true;
        }
    }    
    
    /** 
     * 在业务处理器处理请求执行完成后,生成视图之前执行的动作    
     * 可在modelAndView中加入数据，比如当前时间 
     */  
    @Override    
    public void postHandle(HttpServletRequest request,    
            HttpServletResponse response, Object handler,    
            ModelAndView modelAndView) throws Exception {      
    }    
    
    /**  
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等   
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()  
     */    
    @Override    
    public void afterCompletion(HttpServletRequest request,    
            HttpServletResponse response, Object handler, Exception ex)    
            throws Exception {  
    }    
  
}    
/**  
 * 在业务处理器处理请求之前被调用  
 * 如果返回false  
 *     从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链 
 * 如果返回true  
 *    执行下一个拦截器,直到所有的拦截器都执行完毕  
 *    再执行被拦截的Controller  
 *    然后进入拦截器链,  
 *    从最后一个拦截器往回执行所有的postHandle()  
 *    接着再从最后一个拦截器往回执行所有的afterCompletion()  
 */ 