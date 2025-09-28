package work.chncyl.base.global.aspect;

import cn.hutool.core.bean.BeanUtil;
import work.chncyl.base.global.aspect.annotation.CurrentUser;
import work.chncyl.base.global.security.dto.JwtClaimDto;
import work.chncyl.base.global.security.dto.LoginedUserInfo;
import work.chncyl.base.global.utils.SessionUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 当前登录用户自动注入实现
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持的参数为JwtClaimDto类或LoginedUserInfo类，且被CurrentUser注解标注
        return (parameter.getParameterType().equals(LoginedUserInfo.class)
                || parameter.getParameterType().equals(JwtClaimDto.class))
                // 如果不管有没有CurrentUser注解，都进行注入，则不需要下面这行判断
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        LoginedUserInfo session = SessionUtils.getSession();
        if (parameter.getParameterType().equals(LoginedUserInfo.class)) {
            return session;
        } else if (parameter.getParameterType().equals(JwtClaimDto.class)) {
            return BeanUtil.copyProperties(session, JwtClaimDto.class);
        }
        return null;
    }
}