package work.chncyl.service.user.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.chncyl.service.user.mapper.UserMapper;
import work.chncyl.service.user.entity.User;
import work.chncyl.service.user.service.UserService;
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

}
