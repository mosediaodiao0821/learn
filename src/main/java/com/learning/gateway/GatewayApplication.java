package com.learning.gateway;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.learning.gateway.filter.MyPostFilter;
import com.learning.gateway.filter.MyPreFilter;
import com.netflix.zuul.ZuulFilter;

@EnableZuulProxy
@SpringCloudApplication
public class GatewayApplication 
{
    public static void main( String[] args )
    {
    	new SpringApplicationBuilder(GatewayApplication.class).web(true).run(args);
    }
    
    @Bean
    public ZuulFilter myPreFilter() {
        return new MyPreFilter();
    }
    
    @Bean
    public ZuulFilter myPostilter() {
        return new MyPostFilter();
    }
//  @Bean
//	@LoadBalanced //ribbon
//	public RestTemplate restTemplate() {  // equals to RestTemplate restTemplate = new RestTemplate()
//		return new RestTemplate();
//	}
}
