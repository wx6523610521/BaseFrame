package work.chncyl.service.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.service.user.mapper.AccountMapper;
import work.chncyl.service.user.entity.Account;
import work.chncyl.service.user.service.AccountService;
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{

}
