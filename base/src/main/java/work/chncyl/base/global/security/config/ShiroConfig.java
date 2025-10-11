package work.chncyl.base.global.security.config;

import work.chncyl.base.global.security.CustomerRealm;
import work.chncyl.base.global.security.JwtFilter;
import lombok.Data;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Data
public class ShiroConfig implements WebMvcConfigurer {
    /**
     * 额外增加系统受限资源
     */
    private static final String[] AUTHC_URL = {"system.js"};
    /**
     * 允许匿名访问资源
     */
    private static final String[] ANON_URL = {
            "/",
            "/druid/**",
            // 静态资源
            "/**/*.js",
            "/**/*.css",
            "/**/*.html",
            "/**/*.svg",
            "/**/*.pdf",
            "/**/*.jpg",
            "/**/*.png",
            "/**/*.ico",
            "/api/favicon.ico",
            // 字体
            "/**/*.ttf",
            "/**/*.woff",
            "/**/*.woff2",
            // 接口文档
            "/doc.html",
            "/swagger-ui.html",
            "/swagger**/**",
            "/webjars/**",
            "/v2/**"};

    /**
     * ShiroFilter过滤请求配置
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 给ShiroFilter配置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置系统公共资源
        for (String s : ANON_URL) {
            filterChainDefinitionMap.put(s, "anon");
        }
        // 明确指定 /api/doc.html 为匿名访问
        filterChainDefinitionMap.put("/api/doc.html", "anon");

        // 配置系统受限资源
        for (String s : AUTHC_URL) {
            filterChainDefinitionMap.put(s, "authc");
        }
        // 通用路径放在最后
        filterChainDefinitionMap.put("/**", "jwt");
        // 注意过滤器配置顺序 不能颠倒
        // 配置退出 过滤器, 其中的具体退出代码 Shiro 已经替我们实现了，登出后跳转配置的 loginUrl
        filterChainDefinitionMap.put("/logout", "logout");
        // 配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/unauth");
        // 未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 添加自己的过滤器并且取名为 jwt
        Map<String, Filter> filterMap = new HashMap<>(1);
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建安全管理器
     */
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    /**
     * 自定义Realm的bean
     */
    @Bean
    public Realm getRealm() {
        return new CustomerRealm();
    }

    /**
     * swaggerui
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath*:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath*:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath*:/META-INF/resources/webjars/");
    }
}