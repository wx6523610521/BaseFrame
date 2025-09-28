package work.chncyl.service.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.chncyl.service.user.entity.Account;
import org.apache.ibatis.annotations.Param;

public interface AccountMapper extends BaseMapper<Account> {
    Boolean refreshUserType(@Param("userId") Integer userId, @Param("userType") int userType);

    Account getAccountByUserId(@Param("userId") Integer userId);
}