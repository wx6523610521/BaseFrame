package work.chncyl.base.global.security;

import org.apache.commons.lang3.StringUtils;
import work.chncyl.base.global.security.dto.JwtToken;
import work.chncyl.base.global.security.dto.LoginedUserInfo;
import work.chncyl.base.global.security.utils.TokenUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collections;

/**
 * 自定义realm 用于配置认证和权限等
 */
public class CustomerRealm extends AuthorizingRealm {

    /**
     * 生效条件，因为realm可以有很多个，所以需要进行设置
     * 这里表示支持的是自定义的JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 用户身份认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("token为空!");
        }
        token = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
        // 校验token有效性
        LoginedUserInfo loginUser = null;
        if (TokenUtil.verify(token)) {
            loginUser = TokenUtil.getLoginUser(token);
        } else {
            throw new AuthenticationException("token无效!");
        }
        return new SimpleAuthenticationInfo(loginUser, token, getName());
    }

    /**
     * 授权信息（角色/权限）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LoginedUserInfo sysUser = (LoginedUserInfo) principals.getPrimaryPrincipal();
        // 获取当前设置的管理组织
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(Collections.singleton(sysUser.getRoleId()));
        info.addStringPermissions(sysUser.getPermissions());

        return info;
    }

}