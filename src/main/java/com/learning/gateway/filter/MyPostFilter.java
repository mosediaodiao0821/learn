package com.learning.gateway.filter;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
/**
 * post:返回response之后执行filter 
 * @author lst
 *
 */
public class MyPostFilter extends ZuulFilter {
	private static final Logger log = LoggerFactory.getLogger(MyPostFilter.class);
	
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		InputStream inputStream = ctx.getResponseDataStream();
		String responseData = "";
		try {
			responseData = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ctx.setResponseBody(responseData);
		log.info("res_body:"+responseData);
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
		return "post";
	}
}
