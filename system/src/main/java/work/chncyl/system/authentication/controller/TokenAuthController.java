package work.chncyl.system.authentication.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import work.chncyl.base.global.aspect.annotation.AllowAnonymous;
import work.chncyl.base.global.aspect.annotation.CurrentUser;
import work.chncyl.base.global.aspect.annotation.DisabledInterface;
import work.chncyl.base.global.result.ApiResult;
import work.chncyl.base.global.security.dto.JwtClaimDto;
import work.chncyl.base.global.security.dto.LoginedUserInfo;
import work.chncyl.base.global.security.utils.TokenUtil;
import work.chncyl.base.global.utils.RegexUtils;
import work.chncyl.system.authentication.dto.*;
import work.chncyl.system.authentication.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tokenAuth")
@Slf4j
@Api(tags = "A系统授权接口", description = "用于登录、权限鉴定等系统用户操作")
@RequiredArgsConstructor
public class TokenAuthController {
    private final AuthService authService;

    @ApiOperation(value = "获取登录验证码", notes = "返回由数字和大写字母组成的验证码图片")
    @GetMapping("/Captcha")
    @AllowAnonymous
    public void captcha(HttpServletResponse response, @RequestParam(value = "codeId") String codeId) throws IOException, FontFormatException {
        if (StrUtil.isBlank(codeId)) {
            throw new RuntimeException("参数缺失");
        }
        authService.captcha(response, codeId);
    }

    @PostMapping("/siginSkip")
    @DisabledInterface
    @AllowAnonymous
    public ApiResult<LoginedResult> siginSkip(@RequestBody LoginModel dto) {
        return ApiResult.success(authService.login(dto.getUserName(), dto.getPassword()));
    }

    @ApiOperation(value = "用户登录接口")
    @PostMapping("/login")
    @AllowAnonymous
    public ApiResult<LoginedResult> login(@RequestBody LoginModel dto) {
        if (StrUtil.hasBlank(dto.getCode(), dto.getCodeId())) {
            return ApiResult.error500("验证码不能为空");
        }
        if (!authService.veriryCaptcha(dto.getCodeId(), dto.getCode())) {
            return ApiResult.error500(("验证码错误或已过期，请重新输入！"));
        }
        return ApiResult.success(authService.login(dto.getUserName(), dto.getPassword()));
    }

    @PostMapping("/simpleSignAndLogin")
    @AllowAnonymous
    @DisabledInterface
    public ApiResult<LoginedResult> simpleSignAndLogin(@RequestBody UserInfoInput simpleSignInput) {
        if (StrUtil.isBlank(simpleSignInput.getPhone())) {
            return ApiResult.error500("手机号不能为空");
        }

        return authService.simpleSignAndLogin(simpleSignInput);
    }

    @ApiOperation(value = "当前用户")
    @GetMapping("/cinfo")
    public ApiResult<JwtClaimDto> cinfo(@CurrentUser JwtClaimDto info) {
        return ApiResult.success(info);
    }

    @ApiOperation(value = "当前用户")
    @GetMapping("/cLinfo")
    public ApiResult<JwtClaimDto> cinfo(@CurrentUser LoginedUserInfo info) {
        info.setPhoneNum(RegexUtils.fuzzyPhone(info.getPhoneNum()));
        return ApiResult.success(info);
    }

    @ApiOperation(value = "切换角色")
    @PostMapping("/SetClaims")
    public ApiResult SetClaims(@RequestBody SetClaimsInput input, @CurrentUser LoginedUserInfo claim, HttpServletResponse response) {
        String roleId = input.getRoleId();
        if (StrUtil.isBlank(roleId)) {
            throw new RuntimeException("参数缺失");
        }
        if (!Arrays.stream(claim.getRoleIds().split(",")).collect(Collectors.toList()).contains(roleId)) {
            // 用户没有对应的角色
            throw new RuntimeException("抱歉，设置失败！");
        }

        //重新设置claim
        JwtClaimDto claimDto = new JwtClaimDto();
        BeanUtil.copyProperties(claim, claimDto);
        claimDto.setRoleId(roleId);
        // 签署新的token
        String token = TokenUtil.resign(claimDto);

        // 新的token由response头返回，与token刷新处理方式一致，方便前端同一处理
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return null;
        /*// 实体类响应，用于需要同步更新其他信息
        ClaimAuthticateResultModel rtn=new ClaimAuthticateResultModel();
        BeanUtil.copyProperties(claim,rtn);
        rtn.setToken(token);
        return ApiResult.OK(rtn);*/
    }

    @ApiOperation(value = "注册用户")
    @PostMapping("/register")
    @AllowAnonymous
    public ApiResult<?> register(@RequestBody RegisterInput dto) {
        if (StrUtil.hasBlank(dto.getCode(), dto.getCodeId())) {
            return ApiResult.error500("验证码不能为空");
        }
        if (!authService.veriryCaptcha(dto.getCodeId(), dto.getCode())) {
            return ApiResult.error500(("验证码错误或已过期，请重新输入！"));
        }
        return ApiResult.success(authService.register(dto));
    }

    @ApiOperation(value = "获取加密publicKey")
    @GetMapping("/publicKey")
    @AllowAnonymous
    public ApiResult<String> publicKey(String mark) throws NoSuchAlgorithmException {
        return ApiResult.success(authService.publicKey(mark));
    }

    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logout")
    public ApiResult<?> LoginOut() {
        boolean failure = TokenUtil.failure();
        SecurityUtils.getSubject().logout();
        return ApiResult.success(null);
    }

}