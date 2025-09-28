package work.chncyl.base.global.security.utils;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.RegisteredPayload;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import work.chncyl.base.global.redis.RedisUtils;
import work.chncyl.base.global.security.JwtFilter;
import work.chncyl.base.global.security.dto.JwtClaimDto;
import work.chncyl.base.global.security.dto.LoginedUserInfo;
import work.chncyl.base.global.security.dto.RoleInfo;
import work.chncyl.base.global.utils.LocalCacheUtil;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.jwt.RegisteredPayload.ISSUED_AT;
import static work.chncyl.base.global.Constants.*;

/**
 * token工具，用于token的签发、校验、解析
 */
@Data
@Component
public class TokenUtil {
    /**
     * 加密密钥
     */
    @Value("${security.secret-key}")
    private String secretKey;
    /**
     * 过期时间（秒）
     */
    @Value("${security.expire-second}")
    public int expireTime;
    /**
     * token是否允许挪用
     */
    @Value("${security.can-redeployed}")
    public boolean canRedeployed = true;

    //密钥盐
    public static String TOKEN_SECRET;
    //token到期时间，秒为单位
    public static int EXPIRE_TIME;

    public static boolean redeployed;
    public static final SM3 sm3 = SmUtil.sm3();
    // 签名在进行多线程使用时，线程不安全，需要在使用时创建，不能使用全局，即使签名对象不变
//    private static JWTSigner SIGNER = null;

    private static Set<String> param = null;

    @PostConstruct
    public void init() {
        // 在 @PostConstruct 方法中将非静态成员的值赋给静态成员
        TOKEN_SECRET = secretKey;
        EXPIRE_TIME = expireTime;
        redeployed = canRedeployed;
    }

    /**
     * 生成token
     */
    public static String sign(LoginedUserInfo dto) {
        if (StringUtils.isBlank(TOKEN_SECRET)) {
            throw new RuntimeException("未设置安全密钥");
        }
        JwtClaimDto claimDto = new JwtClaimDto();

        claimDto.setUserId(dto.getUserId());
        claimDto.setUsername(dto.getUsername());
        claimDto.setRoleId(dto.getRoleId());

        // token有效期
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRE_TIME));
        // 签发时间
        long issuedTime = new Date().getTime();

        String jwtId = sm3.digestHex(dto.getUsername());
        Map<String, Object> objectMap = BeanUtil.beanToMap(claimDto);
        if (param == null) {
            param = objectMap.keySet();
        }
        // 缓存用户信息，保存refreshToken，并设置最短过期时间为30分钟
        int refreshTokenTime = EXPIRE_TIME < (15 * 60) ? (30 * 60) : (EXPIRE_TIME * 2);

        // 清楚权限信息，只保留基本信息
        dto.setUserRoles(null);
        dto.setPermissions(null);

        // 本地缓存
        LocalCacheUtil.put(USER_INFO_KEY + jwtId, dto, 5 * 60 * 1000);
        // 用户信息
        RedisUtils.set(USER_INFO_KEY + jwtId, dto, refreshTokenTime);
        // refreshToken刷新token
        RedisUtils.set(REFRESH_TOKEN_KEY + jwtId, issuedTime, refreshTokenTime);

        return JWT.create()
                .setJWTId(jwtId)
                .setIssuedAt(new Date(issuedTime))
                .setExpiresAt(exprireDate)
                .addPayloads(objectMap)
                .sign(JWTSignerUtil.hs256(TOKEN_SECRET.getBytes()));
    }

    /**
     * 重新签署token，用于角色切换时，重新确定所属党组织、角色等信息
     */
    public static String resign(JwtClaimDto dto) {
        String jwtId = getJwtId();
        // 签发时间
        long issuedTime = new Date().getTime();
        // 重签信息
        Map<String, Object> objectMap = BeanUtil.beanToMap(dto);
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRE_TIME));

        //  refreshToken 沿用之前的过期时间，与用户信息同步
        long expire = RedisUtils.getExpire(REFRESH_TOKEN_KEY + jwtId, TimeUnit.SECONDS);
        RedisUtils.set(REFRESH_TOKEN_KEY + jwtId, issuedTime, expire);
        return JWT.create()
                .setJWTId(jwtId)
                .setIssuedAt(new Date(issuedTime))
                .setExpiresAt(exprireDate)
                .addPayloads(objectMap)
                .sign(JWTSignerUtil.hs256(TOKEN_SECRET.getBytes()));
    }

    /**
     * 刷新token过期时间
     *
     * @param currentTime 签署时间，应保证与refreshToken签署时间一致，交由调用方维护
     */
    public static String refreshToken(String token, long currentTime) {
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRE_TIME));
        return JWT.of(token).setIssuedAt(new Date(currentTime)).setExpiresAt(exprireDate).sign(JWTSignerUtil.hs256(TOKEN_SECRET.getBytes()));
    }

    /**
     * 获取jwtId
     */
    public static String getJwtId() {
        return getJwtId(getToken());
    }

    /**
     * 获取jwtId
     */
    public static String getJwtId(String token) {
        if (StringUtils.isBlank(token)) return null;
        final JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload(RegisteredPayload.JWT_ID);
    }

    /**
     * 获取签发时间
     */
    public static Date getIssuedTime(String token) {
        if (StringUtils.isBlank(token)) return null;
        return (Date) JWTUtil.parseToken(token).getPayload(ISSUED_AT);
    }

    /**
     * token验证
     */
    public static Boolean verify(String token) {
        if (StringUtils.isBlank(token)) return false;
        JWTValidator validator = JWTValidator.of(token);
        try {
            if (!redeployed) {
                // 日期校验，两个校验不能合并，上层调用会根据TokenExpiredException异常尝试去刷新token
                validator.validateDate();
            }
        } catch (Exception e) {
            throw new TokenExpiredException("", Instant.now());
        }
        try {
            // 签名校验
            validator.validateAlgorithm(JWTSignerUtil.hs256(TOKEN_SECRET.getBytes()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAccount(String token) {
        if (StringUtils.isBlank(token)) return null;
        final JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("username");
    }

    public static LoginedUserInfo getLoginUser(String token) {
        String jwtId = getJwtId(token);
        if (jwtId == null) {
            return null;
        }
        
        LoginedUserInfo info = LocalCacheUtil.get(USER_INFO_KEY + jwtId);
        if (info == null) {
            info = RedisUtils.get(USER_INFO_KEY + jwtId);
            if (info == null) {
                return null;
            }
            // 缓存到本地，避免频繁访问Redis
            LocalCacheUtil.put(USER_INFO_KEY + jwtId, info, 5 * 60 * 1000);
        }
        
        // 如果权限信息已经加载过，直接返回
        if (info.getUserRoles() != null && info.getPermissions() != null) {
            return info;
        }
        
        // 加载权限信息
        String[] split = info.getRoleIds().split(",");
        List<RoleInfo> roleInfos = PermissionCacheUtil.RoleCache.simpleGet(Arrays.asList(split));
        List<String> strings = PermissionCacheUtil.PermissionCache.get(Arrays.asList(split));
        info.setUserRoles(roleInfos);
        info.setPermissions(strings);
        
        return info;
    }

    public static String getToken() {
        String token;
        if (StringUtils.isBlank((token = JwtFilter.threadLocal.get())) && RequestContextHolder.getRequestAttributes() != null) {
            token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(ACCESS_TOKEN);
        }
        if (StringUtils.isBlank(token)) return null;
        return token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
    }

    public static String getValueByKey(String key) {
        if (param != null && !param.contains(key)) {
            return null;
        }
        String token = getToken();
        return getValueByKey(token, key);
    }

    public static String getValueByKey(String token, String key) {
        if (StringUtils.isBlank(token)) return null;
        final JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload(key);
    }

    public static boolean failure() {
        String token = getToken();
        String jwtId = getJwtId(token);
        // 删除缓存的用户信息
        Boolean delete = RedisUtils.delete(jwtId);
        // 删除refreshToken
        return delete && RedisUtils.delete(REFRESH_TOKEN_KEY + jwtId);
    }
}
