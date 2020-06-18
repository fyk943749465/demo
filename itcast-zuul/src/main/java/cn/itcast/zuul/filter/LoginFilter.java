package cn.itcast.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class LoginFilter extends ZuulFilter {

    /**
     * 过滤器类型,有四种选择 pre route post error
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 执行顺序, 返回值越小,优先级越高
     * @return
     */
    @Override
    public int filterOrder() {
        return 10;
    }

    /**
     * 是否执行run过滤器
     * true: 执行run方法
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 编写过滤器的业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        // 初始化context 上下文对象,
        RequestContext requestContext = RequestContext.getCurrentContext();
        // 获取request 对象
        HttpServletRequest request = requestContext.getRequest();
        // 获取参赛
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            // 拦截,不转发请求
            requestContext.setSendZuulResponse(false);
            // 响应状态吗
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            // 设置响应的提示
            requestContext.setResponseBody("request error!");
        }

        // 返回值为null, 表示过滤器不执行任何动作
        return null;
    }
}
