package work.chncyl.base.global.utils;

import work.chncyl.base.global.security.dto.LoginedUserInfo;
import org.apache.shiro.SecurityUtils;

public class SessionUtils {
    public static LoginedUserInfo getSession() {
        return (LoginedUserInfo) SecurityUtils.getSubject().getPrincipal();
    }
}
