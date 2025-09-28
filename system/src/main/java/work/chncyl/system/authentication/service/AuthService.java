package work.chncyl.system.authentication.service;

import work.chncyl.base.global.result.ApiResult;
import work.chncyl.system.authentication.dto.LoginedResult;
import work.chncyl.system.authentication.dto.RegisterInput;
import work.chncyl.system.authentication.dto.UserInfoInput;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface AuthService {

    void captcha(HttpServletResponse response, String codeId) throws IOException, FontFormatException;

    boolean veriryCaptcha(String codeId, String code);

    LoginedResult login(String userName, String password);

    boolean register(RegisterInput input);

    ApiResult<LoginedResult> simpleSignAndLogin(UserInfoInput simpleSignInput);

    String publicKey(String mark) throws NoSuchAlgorithmException;
}
