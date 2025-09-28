package work.chncyl.system.authentication.mapper;

import work.chncyl.base.global.security.dto.LoginedUserInfo;
import org.apache.ibatis.annotations.Param;

public interface AuthMapper {
    LoginedUserInfo getLoginUserInfo(@Param("userName") String userName);
}
