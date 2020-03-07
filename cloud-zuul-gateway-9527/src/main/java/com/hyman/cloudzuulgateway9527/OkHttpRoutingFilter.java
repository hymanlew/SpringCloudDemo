package com.hyman.cloudzuulgateway9527;

import com.google.common.net.MediaType;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * filterType()：定义过滤器的类型，它有4种类型，分别是pre、post、routing和error
 * filterOrder()：过滤顺序，它是一个Int类型的值，值越小，越早执行该过滤器
 * shouldFilter()：表示该过滤器是否过滤逻辑，如果为true，则执行run方法，如果为false，则不执行run方法
 * Object run()：写具体的过滤逻辑
 *
 * 展示一个简单的例子,判断请求的参数中是否有token这个参数，如果没有传，则请求不会被路由到具体的服务实例，直接返回响应。
 */
@Component
public class OkHttpRoutingFilter extends ZuulFilter {

    @Autowired
    private ProxyRequestHelper helper;

    /**
     * 预筛选器在请求上下文中设置数据，以便在下游筛选器中使用。主要用例是设置路由筛选器所需的信息。
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        Object accessToken = request.getParameter("token");
        if (null == accessToken) {
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(404);
            try {
                context.getResponse().getWriter().write("token is empty");

            } catch (Exception e) {
            }
            return null;
        }
        return null;
    }
}

