package work.chncyl.system.authentication.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.security.dto.LoginedUserInfo;
import work.chncyl.base.global.security.dto.RoleInfo;
import work.chncyl.base.global.security.utils.TokenUtil;
import work.chncyl.base.global.utils.CheckPwdUtils;
import work.chncyl.base.global.utils.EncryDecryUtils;
import work.chncyl.base.global.utils.RegexUtils;
import work.chncyl.base.global.utils.SpringUtils;
import work.chncyl.service.permission.dto.input.Associate;
import work.chncyl.service.permission.entity.Role;
import work.chncyl.service.permission.mapper.PermissionsMapper;
import work.chncyl.service.permission.mapper.RoleMapper;
import work.chncyl.service.user.entity.Account;
import work.chncyl.service.user.entity.User;
import work.chncyl.service.user.enums.EnumAccountStatus;
import work.chncyl.service.user.mapper.AccountMapper;
import work.chncyl.service.user.mapper.UserMapper;
import work.chncyl.system.authentication.dto.LoginedResult;
import work.chncyl.system.authentication.dto.RegisterInput;
import work.chncyl.system.authentication.dto.UserInfoInput;
import work.chncyl.system.authentication.mapper.AuthMapper;
import work.chncyl.system.authentication.service.AuthService;
import com.wf.captcha.SpecCaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wf.captcha.base.Captcha.TYPE_ONLY_CHAR;
import static work.chncyl.base.global.Constants.CAPTCHA_CODES;
import static work.chncyl.base.global.Constants.PASSWORD_ERROR_NUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${security.password-error-count:5}")
    private int accessFailedCount;
    @Value("${security.account-lock-minutes:5}")
    private int lockTime;

    private final RoleMapper rolesMapper;
    private final PermissionsMapper permissionsMapper;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

    private final AuthMapper authMapper;


    @Override
    public void captcha(HttpServletResponse response, String codeId) throws IOException, FontFormatException {
        SpecCaptcha specCaptcha = new SpecCaptcha(105, 35, 4);
        specCaptcha.setCharType(TYPE_ONLY_CHAR);
        specCaptcha.setFont(SpecCaptcha.FONT_9);
        String verCode = specCaptcha.text().toLowerCase();
        RedisUtils.set(CAPTCHA_CODES + codeId, verCode, 3L, TimeUnit.MINUTES);
        ServletOutputStream outputStream = response.getOutputStream();
        specCaptcha.out(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public boolean veriryCaptcha(String codeId, String code) {
        boolean equals = code.equals(RedisUtils.get(CAPTCHA_CODES + codeId));
        RedisUtils.delete(CAPTCHA_CODES + codeId);
        return equals;
    }

    @Override
    public LoginedResult login(String userName, String password) {
        // 先查看是否已被锁定
        Long o = RedisUtils.get(PASSWORD_ERROR_NUM + userName);
        if (o != null && o >= accessFailedCount) {
            // 查看剩余时间(毫秒),默认整数会舍去小数位，为防止出现0秒的提示,提取毫秒
            Long expire = RedisUtils.getExpire(PASSWORD_ERROR_NUM + userName, TimeUnit.MILLISECONDS);
            if (expire > 0) {
                // 默认int会舍去小数位，为防止出现0秒的提示，向上取整
                throw new RuntimeException("用户已被锁定，请等候" + (Math.ceil((double) expire / 1000)) + "秒之后再登录");
            }
        }
        //根据用户名查询用户基本信息
        LoginedUserInfo userInfo = authMapper.getLoginUserInfo(userName);
        if (userInfo == null) {
            throw new RuntimeException("用户未找到");
        }
        if (!password.equals(userInfo.getAccountPwd())) {
            Long incre = RedisUtils.incre(PASSWORD_ERROR_NUM + userName, lockTime);
            if (incre >= accessFailedCount) {
                throw new RuntimeException("用户已被锁定，请等候" + lockTime + "分钟后再登录");
            }
            throw new RuntimeException("密码错误");
        }
        if (userInfo.getAccountStatus() == null || !EnumAccountStatus.ENABLE.getValue().equals(userInfo.getAccountStatus())) {
            throw new RuntimeException("账号未启用，请联系管理员");
        }
        if (userInfo.getRoleIds() == null || StrUtil.isBlank(userInfo.getRoleIds())) {
            throw new RuntimeException("账号未授予权限，请联系管理员");
        }

        /*if (detail.getIsChecked() != null && detail.getIsChecked() != 1) {
            throw new RuntimeException("账号未通过审核，请联系管理员");
        }*/

        // 获取权限信息
        List<String> roleIds = Arrays.asList(userInfo.getRoleIds().split(","));
        List<Role> roles = rolesMapper.selectList(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));
        List<RoleInfo> roleInfos = BeanUtil.copyToList(roles, RoleInfo.class);
        userInfo.setRoleId(roleIds.get(0));

        userInfo.setUserRoles(roleInfos);
        List<String> permission = permissionsMapper.selectPermissionByRole(roleIds);
        userInfo.setPermissions(permission);

        // 登录验证完成，清除失败记录， 更新最后登录时间
        RedisUtils.delete(PASSWORD_ERROR_NUM + userName);
        accountMapper.update(null, new LambdaUpdateWrapper<Account>()
                .set(Account::getLastLoginTime, new Date())
                .eq(Account::getUserId, userInfo.getUserId())
        );

        //生成token
        String token = TokenUtil.sign(userInfo);

        // 封装返回内容
        LoginedResult result = new LoginedResult();
        result.setToken(token);
        result.setNickName(userInfo.getNickName());
        result.setHeadImage(userInfo.getHeadImage());
        if (StrUtil.isNotBlank(userInfo.getPhoneNum())) {
            // 手机号模糊处理
            result.setPhoneNum(RegexUtils.fuzzyPhone(userInfo.getPhoneNum()));
        }
        return result;
    }

    @Override
    @Transactional
    public boolean register(RegisterInput input) {
        //根据用户名查询用户基本信息
        long count = accountMapper.selectCount(new LambdaQueryWrapper<Account>().eq(Account::getAccountName, input.getAccountName()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 密码复杂度验证
        String accountPwd = input.getAccountPwd();
        try {
            if (StringUtils.isNotBlank(input.getMark())) {
                accountPwd = EncryDecryUtils.decryptByMark(accountPwd, input.getMark());
            } else {
                accountPwd = EncryDecryUtils.decrypt(accountPwd);
            }

        } catch (Exception e) {
            // todo 密码解密失败处理方式
            log.error("密码解密失败：" + e.getMessage());
            accountPwd = accountPwd;
        }
        boolean b = CheckPwdUtils.evalPWD(accountPwd);
        if (!b) {
            throw new RuntimeException("密码不符合规范");
        }

        // 用户信息
        User user = BeanUtil.copyProperties(input, User.class);
        user.setCreateTime(new Date());
        user.setIsDeleted(false);
        userMapper.insert(user);

        // 获取默认角色
        List<Role> role = rolesMapper.getDefaultRole();
        if (role != null && !role.isEmpty()) {
            Associate associate = new Associate(user.getId(), role.stream().map(Role::getId).collect(Collectors.toList()));
            rolesMapper.associateRole(associate);
        }

        // 账号信息
        Account account = BeanUtil.copyProperties(input, Account.class);
        account.setAccountStatus(EnumAccountStatus.ENABLE.getValue().byteValue());
        account.setAccountType(input.getAccountType());
        account.setUserId(user.getId());
        // 保存md5加密的密码
        account.setAccountPwd(new Digester(DigestAlgorithm.MD5).digestHex(account.getAccountPwd()));
        accountMapper.insert(account);

        return true;
    }

    @Override
    public ApiResult<LoginedResult> simpleSignAndLogin(UserInfoInput simpleSignInput) {

        // 根据手机号查询用户
        Account account = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getAccountName, simpleSignInput.getPhone()));

        if (account != null) {
            // 用户存在，直接登录
            LoginedResult loginedResult = SpringUtils.getBean(AuthService.class).login(simpleSignInput.getPhone(), new Digester(DigestAlgorithm.MD5).digestHex("123456"));

            return ApiResult.success(loginedResult);
        } else {
            // 用户不存在，注册新用户
            RegisterInput registerInput = new RegisterInput();
            registerInput.setAccountName(simpleSignInput.getPhone());
            registerInput.setPhoneNum(simpleSignInput.getPhone());
            registerInput.setUserName(simpleSignInput.getName());
            registerInput.setCompanyName(simpleSignInput.getCompany());
            // 默认密码123456加密
            registerInput.setAccountPwd("123456");
            // 设置默认昵称为姓名
            registerInput.setNickname(simpleSignInput.getName());
            try {
                boolean registered = SpringUtils.getBean(AuthService.class).register(registerInput);
                if (registered) {
                    // 注册成功后登录
                    LoginedResult loginedResult = SpringUtils.getBean(AuthService.class).login(simpleSignInput.getPhone(), new Digester(DigestAlgorithm.MD5).digestHex("123456"));
                    return ApiResult.success(loginedResult);
                } else {
                    log.error("用户注册失败：记录数据时返回false");
                    return ApiResult.error500("用户信息记录失败");
                }
            } catch (Exception e) {
                log.error("用户注册失败：" + e.getMessage());
                return ApiResult.error500("用户信息记录失败");
            }
        }
    }

    @Override
    public String publicKey(String mark) throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(mark)) {
            return EncryDecryUtils.getDefaultPublicKey();
        }
        return EncryDecryUtils.generateTemporaryKeyPair(mark).getPublicKey();
    }
}
