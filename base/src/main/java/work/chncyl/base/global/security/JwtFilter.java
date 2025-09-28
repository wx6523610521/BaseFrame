package work.chncyl.base.global.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;
import work.chncyl.base.global.Constants;
import work.chncyl.base.global.aspect.annotation.AllowAnonymous;
import work.chncyl.base.global.aspect.annotation.DisabledInterface;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.result.ResultUtil;
import work.chncyl.base.global.security.dto.JwtToken;
import work.chncyl.base.global.security.utils.TokenUtil;
import work.chncyl.base.global.utils.SpringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 自定义jwt过滤器，对token进行处理
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
    public static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
    private static String activeProfile;

    /**
     * 拦截器的前置方法，此处进行跨域处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        threadLocal.remove();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Resquest-Headers"));
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            // OPTION请求直接返回正常
            httpServletResponse.setStatus(HttpStatus.OK.value());
        }
        //如果不带token，不去验证shiro
        /*if (!isLoginAttempt(request, response)) {
            responseError(httpServletResponse, "no token");
            return false;
        }*/
        threadSetToken(request);

        return super.preHandle(request, response);
    }

    private void threadSetToken(ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader(Constants.ACCESS_TOKEN);
        if (StringUtils.isNotBlank(token)) {
            threadLocal.set(token);
        }
    }

    /**
     * 获取当前激活的环境配置
     */
    public static String getActiveProfile() {
        if (activeProfile == null) {
            synchronized (JwtFilter.class) {
                if (activeProfile == null) {
                    try {
                        activeProfile = SpringUtils.getEnvironmentProperty("spring.profiles.active");
                        System.out.println("获取到激活的环境配置: " + activeProfile);
                    } catch (Exception e) {
                        System.out.println("获取环境配置失败: " + e.getMessage());
                        activeProfile = "pro"; // 默认值
                    }
                }
            }
        }
        return activeProfile;
    }

    /**
     * 判断是否允许通过
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(httpServletRequest);
        RequestMappingHandlerMapping mapping = ctx.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        HandlerExecutionChain handler;

        try {
            handler = mapping.getHandler(httpServletRequest);
            if (null == handler) {
                ResultUtil.responseError(response, 404, "接口不存在，请联系管理员");
                return false;
            }
            HandlerMethod handlerClass = (HandlerMethod) handler.getHandler();
            // 判断是否为禁用类/接口
            if (!getActiveProfile().equals("dev")) {
                if (handlerClass.getMethod().isAnnotationPresent(DisabledInterface.class)
                        || handlerClass.getBeanType().isAnnotationPresent(DisabledInterface.class)
                ) {
                    ResultUtil.responseError(response, 404, "接口不存在，请联系管理员");
                    return false;
                }
            }

            String token = getToken(request);
            if (StringUtils.isBlank(token)) {
                // 没有token,判断是否允许匿名访问
                if (handlerClass.getMethod().isAnnotationPresent(AllowAnonymous.class)
                        || handlerClass.getBeanType().isAnnotationPresent(AllowAnonymous.class)
                ) {
                    return true;
                }
            }
            // 交由 executeLogin 进行登录状态验证
            return executeLogin(request, response);
        } catch (Exception e) {
            System.out.println("错误" + e);
//            throw new ShiroException(e.getMessage());
            responseError(response, "shiro fail");
            return false;
        }
    }

    /**
     * 创建shiro token
     * executeLogin方法会调用该方法，并将创建好的token交由 realm进行验证
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        return new JwtToken(getToken(request));
    }

    /**
     * 是否携带token
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String token = ((HttpServletRequest) request).getHeader(Constants.ACCESS_TOKEN);
        return token != null;
    }

    /**
     * isAccessAllowed 方法返回false时调用，验证失败
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        this.sendChallenge(request, response);
        responseError(response, "token verify fail");
        return false;
    }

    /**
     * shiro验证成功调用
     * 判断token是否为盗用token(签发时间与记录的签发时间不同)
     * 并尝试刷新过期token
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String jwttoken = (String) token.getPrincipal();
        if (jwttoken != null) {
            try {
                if (TokenUtil.verify(jwttoken)) {
                    //判断Redis是否存在所对应的RefreshToken
                    String jwtId = TokenUtil.getJwtId(jwttoken);
                    // 获取当前token签发时间
                    Long issuedTime = TokenUtil.getIssuedTime(jwttoken).getTime();
                    // 获取refreshToken的签发时间
                    Long currentTimeMillisRedis = RedisUtils.get(Constants.REFRESH_TOKEN_KEY + jwtId);
                    return issuedTime.equals(currentTimeMillisRedis);
                }
                return false;
            } catch (Exception e) {
                // 如果过期了，尝试刷新token
                if (e instanceof TokenExpiredException) {
                    return refreshToken(request, response);
                }
            }
        }
        return true;
    }

    /**
     * 刷新AccessToken，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
     */
    private boolean refreshToken(ServletRequest request, ServletResponse response) {
        String token = getToken(request);
        String jwtId = TokenUtil.getJwtId(token);
        // token中记录的签发时间
        Long currentTime = TokenUtil.getIssuedTime(token).getTime();
        // 判断Redis中RefreshToken是否存在
        if (RedisUtils.hasKey(Constants.REFRESH_TOKEN_KEY + jwtId)) {
            // Redis中RefreshToken还存在，获取RefreshToken记录的签发时间
            Long currentTimeMillisRedis = RedisUtils.get(Constants.REFRESH_TOKEN_KEY + jwtId);
            // Token中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
            if (currentTimeMillisRedis.equals(currentTime)) {
                // 获取当前最新时间戳
                Long currentTimeMillis = System.currentTimeMillis();
                // 刷新RefreshToken
                RedisUtils.set(Constants.REFRESH_TOKEN_KEY + jwtId, currentTimeMillis, TokenUtil.EXPIRE_TIME * 2L);
                // 同步刷新缓存的用户信息
                RedisUtils.expire(Constants.USER_INFO_KEY + jwtId, TokenUtil.EXPIRE_TIME * 2L);
                // 刷新AccessToken，设置签发时间为当前最新时间戳
                token = TokenUtil.refreshToken(token, currentTimeMillis);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Authorization", token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
                return true;
            }
        }
        return false;
    }
    private String getToken(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(Constants.ACCESS_TOKEN);

        if (StringUtils.isBlank(token)) {
            token = httpServletRequest.getParameter("token");
        }
        if (StringUtils.isBlank(token)) {
            token = "";
        }
        token = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;

        return token;
    }

    private void responseError(ServletResponse response, String msg) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(401);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");

        // 使用 try-with-resources 语句来确保 Writer 能够正确关闭
        try (OutputStream os = httpResponse.getOutputStream()) {
            String rj = new ObjectMapper().writeValueAsString(ApiResult.error401(msg));
            os.write(rj.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在preHandle返回false 或过滤器链中抛出异常时调用
     *
     * @param request   the incoming ServletRequest
     * @param response  the outgoing ServletResponse
     * @param exception any exception thrown during {@link #preHandle preHandle}, {@link #executeChain executeChain},
     *                  or {@link #postHandle postHandle} execution, or {@code null} if no exception was thrown
     *                  (i.e. the chain processed successfully).
     */
    @Override
    public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
        // 清理ThreadLocal中的数据
        threadLocal.remove();
        super.afterCompletion(request, response, exception);
    }

    /**
     * 清理行为（如退出后）
     */
    @Override
    public void destroy() {
        threadLocal.remove();
        super.destroy();
    }

    /*public static void responseError(ServletResponse response, String msg) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(401);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        Result jsonResult = new Result(401, msg);
        OutputStream os = null;
        try {
            os = httpResponse.getOutputStream();
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setStatus(401);
            os.write(new ObjectMapper().writeValueAsString(jsonResult).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}