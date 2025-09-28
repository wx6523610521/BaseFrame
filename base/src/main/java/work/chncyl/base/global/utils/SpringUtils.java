package work.chncyl.base.global.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring工具
 * @author chncyl
 */
@Component
public class SpringUtils implements ApplicationContextAware, EnvironmentAware {
    private static ApplicationContext applicationContext;

    private static Environment environment;

    public static <T> T getBean(Class<T> clz) {
        return applicationContext.getBean(clz);
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clz) {
        return applicationContext.getBean(beanName, clz);
    }

    public static String getEnvironmentProperty(String property) {
        return environment.getProperty(property);
    }

    public static <T> T getEnvironmentProperty(String property, Class<T> targetType) {
        return environment.getProperty(property, targetType);
    }


    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    /**
     * 获取HttpServletResponse
     */
    public static HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext == null)
            SpringUtils.applicationContext = applicationContext;
    }

    public void setEnvironment(Environment environment) {
        if (SpringUtils.environment == null)
            SpringUtils.environment = environment;
    }
}