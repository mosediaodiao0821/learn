package com.learning.gateway.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.learning.utils.HttpHelper;
import com.learning.utils.MD5;
import com.learning.utils.MyHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
/**
 * pre:请求执行之前filter 
 * @author lst
 *
 */
public class MyPreFilter extends ZuulFilter {
	private static final Logger log = LoggerFactory.getLogger(MyPreFilter.class);
	
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Map<String, List<String>> qp = new HashMap<String, List<String>>();
        List<String> value = new ArrayList<String>();
        value.add(getIpAddr(request));
        qp.put("dm_ip", value);
        value = new ArrayList<String>();
		value.add("cn");
        qp.put("lang", value);
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			value = new ArrayList<String>();
			value.add(request.getParameter(param));
			qp.put(param, value);
		}
        ctx.setRequestQueryParams(qp);
        String uri = request.getRequestURI();
        log.info("request:"+uri);
        //构造验证请求头
  		try {
  			MyHttpServletRequestWrapper requestWrapper = new MyHttpServletRequestWrapper(request);
  			String body = HttpHelper.getBodyString((ServletRequest)requestWrapper);
  			ctx.addZuulRequestHeader("DMAuthorization", MD5.getMD5("deal" + body + "moon"));
  		} catch (IOException e) {
  			log.error("报错信息：",e);
  		}
		return null;
	}

	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public String filterType() {
		return "pre";
	}
	
	public String getIpAddr(HttpServletRequest httpRequest) {   
	     String ipAddress = null;   
	     //ipAddress = this.getRequest().getRemoteAddr();   
	     ipAddress = httpRequest.getHeader("x-forwarded-for");   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	      ipAddress = httpRequest.getHeader("Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	         ipAddress = httpRequest.getHeader("WL-Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	      ipAddress = httpRequest.getRemoteAddr();   
	     }   
	  
	     //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割   
	     if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
	    	 if(ipAddress.indexOf(",")>0){
	    		 if(ipAddress.indexOf("10.") == 0) {
		    		 ipAddress = ipAddress.split(",")[1].trim();
		    	 } else {
		    		 ipAddress = ipAddress.split(",")[0];
		    	 }
	         }   
	     }
	     log.info("ip:"+ipAddress);
	     return ipAddress;    
	  } 
}
