package org.blkj.enhance;

import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * 页面缓存主要用Filter过滤器对请求的url进行过滤，如果该url在缓存中出现。那么页面数据就从缓存对象中获取，
 * 并以gzip压缩后返回。其速度是没有压缩缓存时速度的3-5倍，效率相当之高！其中页面缓存的过滤器有CachingFilter，
 * 一般要扩展filter或是自定义Filter都继承该CachingFilter。
 * CachingFilter功能可以对HTTP响应的内容进行缓存。这种方式缓存数据的粒度比较粗，例如缓存整张页面。它的优点
 * 是使用简单、效率高，缺点是不够灵活，可重用程度不高。
 * EHCache使用SimplePageCachingFilter类实现Filter缓存。该类继承自CachingFilter，
 * 有默认产生cache key的calculateKey()方法，该方法使用HTTP请求的URI和查询条件来组成key。
 * 也可以自己实现一个Filter，同样继承CachingFilter类,然后覆写calculateKey()方法，生成自定义的key。
 * CachingFilter输出的数据会根据浏览器发送的Accept-Encoding头信息进行Gzip压缩。
 * 在使用Gzip压缩时，需注意两个问题：
 * 1. Filter在进行Gzip压缩时，采用系统默认编码，对于使用GBK编码的中文网页来说，
 * 需要将操作系统的语言设置为：zh_CN.GBK，否则会出现乱码的问题。
 * 2. 默认情况下CachingFilter会根据浏览器发送的请求头部所包含的Accept-Encoding参数值来
 * 判断是否进行Gzip压缩。虽然IE6/7浏览器是支持Gzip压缩的，但是在发送请求的时候却不带该参数。
 * 为了对IE6/7也能进行Gzip压缩，可以通过继承CachingFilter，实现自己的Filter，
 * 然后在具体的实现中覆写方法acceptsGzipEncoding。
 */

public class PageEhCacheFilter extends SimplePageCachingFilter {

	
	private static final Logger log = Logger.getLogger(PageEhCacheFilter.class);
	
	private final static String FILTER_URL_PATTERNS = "patterns";
	private static String[] cacheURLs;

	private void init() throws CacheException 
	{
		String patterns = filterConfig.getInitParameter(FILTER_URL_PATTERNS);
		cacheURLs = StringUtils.split(patterns, ",");
	}
//	protected void qiaohao2New(HttpServletRequest request,HttpServletResponse response) {
//		request.
//	}

	@Override
	protected void doFilter(final HttpServletRequest request,
			final HttpServletResponse response, final FilterChain chain)
					throws AlreadyGzippedException, AlreadyCommittedException,
					FilterNonReentrantException, LockTimeoutException, Exception 
	{
		if (cacheURLs == null) {
			init();
		}

		String url = request.getRequestURI();
		boolean flag = false;
		if (cacheURLs != null && cacheURLs.length > 0) {
			for (String cacheURL : cacheURLs) {
				if (url.contains(cacheURL.trim())) {
					flag = true;
					break;
				}
			}
		}
		//a->b
		
		
		
		// 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
		if (flag) {
			String query = request.getQueryString();
			if (query != null) {
				query = "?" + query;
			}
			log.info("当前请求被缓存：" + url + query);
			
			super.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
		//b->a
	}


	private boolean headerContains(final HttpServletRequest request, final String header, final String value) {
		logRequestHeaders(request);
		final Enumeration<?> accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see net.sf.ehcache.constructs.web.filter.Filter#acceptsGzipEncoding(javax.servlet.http.HttpServletRequest)
	 * <b>function:</b> 兼容ie6/7 gzip压缩
	 * @author hoojo
	 * @createDate 2012-7-4 上午11:07:11
	 */
	@Override
	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return acceptsEncoding(request, "gzip") || ie6 || ie7;
	}

}