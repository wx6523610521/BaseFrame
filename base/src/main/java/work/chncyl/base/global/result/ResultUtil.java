package work.chncyl.base.global.result;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ResultUtil {
    public static void responseError(ServletResponse response, Integer code, String errorMsg) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        work.chncyl.base.global.result.ApiResult jsonResult = new work.chncyl.base.global.result.ApiResult<>(false, code, errorMsg);
        OutputStream os = null;
        try {
            os = httpServletResponse.getOutputStream();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(code);
            os.write(new ObjectMapper().writeValueAsString(jsonResult).getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
