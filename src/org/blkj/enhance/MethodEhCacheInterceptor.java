package org.blkj.enhance;

//数据访问时，触发ehcache的更新

/**拦截以get和find开头的方法（用于缓存结果）*/
import java.io.Serializable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * 对象缓存就是将查询的数据，添加到缓存中，下次再次查询的时候直接从缓存中获取，而不去数据库中查询。
 * 对象缓存一般是针对方法、类而来的，结合Spring的Aop对象、方法缓存就很简单。
 * 这里需要用到切面编程，用到了Spring的MethodInterceptor或是用@Aspect。
 * 
 * 方法拦截器主要是对你要拦截的类的方法进行拦截，然后判断该方法的类路径+方法名称+参数值组合的cache key在
 * 缓存cache中是否存在。如果存在就从缓存中取出该对象，转换成我们要的返回类型。没有的话就把该方法返回的对象
 * 添加到缓存中即可。值得主意的是当前方法的参数和返回值的对象类型需要序列化。
 * 需要在src目录下添加applicationContext.xml完成对MethodCacheInterceptor拦截器的配置，
 * 该配置主意是注入我们的cache对象，哪个cache来管理对象缓存，然后哪些类、方法参与该拦截器的扫描。
 */
public class MethodEhCacheInterceptor implements MethodInterceptor, InitializingBean {
  /*
        该拦截器实现org.aopalliance.intercept.MethodInterceptor接口。
        一旦运行起来(kicks-in)，它首先检查被拦截方法是否被配置为可缓存的。  这将可选择性的配置想要缓存的 bean方法。
        只要调用的方法配置为可缓存， 拦截器将为该方法生成 cache key 并检查该方法返回的结果是否已缓存。 如果已缓存，
        就返回缓存的结果，否则再次调用被拦截方法，并缓存结果供下次调用。
   */
	
	public MethodEhCacheInterceptor() {  
        super();  
    }
	private Cache cache;
    //设置缓存名
    public void setCache(Cache cache) {
        this.cache = cache;
    }
 
    /**
     * 拦截Service/DAO 的方法，并查找该结果是否存在，如果存在就返回cache 中的值;否则，
     * 返回数据库查询结果，并将查询结果放入cache
     * 方法结果必须是可序列化的(serializable)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
    	//表示哪个类调用或触发这个MethodCacheInterceptor，如里的:dao.PersonMagagerImpl  
    	String targetName = invocation.getThis().getClass().getName();
    	
    	//表示哪个方法触发了这个类（MethodCacheInterceptor）方法（invoke）的调用，如这里的:getList  
        String methodName = invocation.getMethod().getName();
  
        //调用的参数,这里没有参数  
        Object[] arguments = invocation.getArguments();
        Object result;
        //这里得出的是:dao.PersonManagerImpl.getList  
        String cacheKey = getCacheKey(targetName, methodName, arguments);
        Element element = null;
      //  synchronized (this) {
            element = cache.get(cacheKey);
            if (element == null) {
                /*
                 * 调用数据访问方法，这里是调用dao.PersonManagerImpl.getList(),并用result保存
                 * 执行的结果（数据访问的结果）,如这里调用了getList()方法，会先打印出
                 * "get Person from DB" ，然后将结果集放入到result里面去，
                 * 由于使用的是自己配置只能放入10个元素的ehcache，所以这里的result是
                 * ArrayList<E> ，它里面存放的是elementData[10]，并将getList得到的结果
                 * 放入到elementData里面去了
                 */  
                result = invocation.proceed();
              /*将cacheKey与数据集连起来，cacheKey是用来标识这个element的标志，
                                        可以有多个element(各自是来自不同的数据访问方法而形成的)，区分它们就是用cacheKey
                                        这里的新生成后的element，含有cacheKey，还在element创建时间，访问时间，还有命令
                                        次数等cache的属性，我觉得它就像是一个小cache一样，下次要不要更新它就要看它的这些属
                                        性来决定。  
               */
                element = new Element(cacheKey, (Serializable) result);
                
                
                cache.put(element);//放入cache中  
            } 
            else {
            	//完成cache操作
            }
      //  }
        return element.getObjectValue();//.getValue();
    }

    /**
     * 获得cache key 的方法，cache key 是Cache 中一个Element 的唯一标识 
     * cache key 包括包名+类名+方法名，如 com.co.cache.service.UserServiceImpl.getAllUser
     */
    private String getCacheKey(String targetName, String methodName, Object[] arguments) {
        StringBuffer sb = new StringBuffer();
        sb.append(targetName).append(".").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append(".").append(arguments[i]);
            }
        }
        return sb.toString();
    }
    //implement InitializingBean，检查cache 是否为空
    public void afterPropertiesSet() throws Exception {
    	 Assert.notNull(cache, "Need a cache. Please use setCache(Cache) create it."); 
    }
}