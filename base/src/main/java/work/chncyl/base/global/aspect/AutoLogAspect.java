package work.chncyl.base.global.aspect;

import com.alibaba.fastjson2.JSONObject;
import work.chncyl.base.global.aspect.annotation.AutoLog;
import work.chncyl.base.global.security.dto.JwtClaimDto;
import work.chncyl.base.global.syslog.SysLog;
import work.chncyl.base.global.syslog.SysLogService;
import work.chncyl.base.global.utils.IPUtils;
import work.chncyl.base.global.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class AutoLogAspect implements CommandLineRunner {

    private SysLogService sysLogService;

    @Pointcut("@annotation(work.chncyl.base.global.aspect.annotation.AutoLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(point, time, result);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
        boolean flag = false;
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            SysLog dto = new SysLog();
            AutoLog syslog = method.getAnnotation(AutoLog.class);

            if (syslog != null) {
                //update-begin-author:taoyan date:
                String content = syslog.value();
                //注解上的描述,操作日志内容
                dto.setLogType(syslog.logType().getMessage());
                dto.setLogContent(content);
                flag = syslog.affectsResults();
            }

            //请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            dto.setMethod(className + ":" + methodName + "()");

            //获取request
            HttpServletRequest request = SpringUtils.getHttpServletRequest();
            //请求的参数
            dto.setRequestParam(getReqestParams(request, joinPoint));
            //设置IP地址
            dto.setIp(IPUtils.getIpAddress(request));
            //获取登录用户信息
            JwtClaimDto sysUser = (JwtClaimDto) SecurityUtils.getSubject().getPrincipal();
            if (sysUser != null) {
                dto.setUserid(sysUser.getUserId());
                dto.setUsername(sysUser.getUsername());
            }
            //耗时
            dto.setCostTime(time);
            dto.setCreateTime(new Date());
            //保存系统日志
            sysLogService.addLog(dto);
        } catch (Exception e) {
            if (flag) {
                throw new RuntimeException(e);
            }
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 获取请求参数
     *
     * @param request:   request
     * @param joinPoint: joinPoint
     */
    private String getReqestParams(HttpServletRequest request, JoinPoint joinPoint) {
        String httpMethod = request.getMethod();

        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            Object[] paramsArray = joinPoint.getArgs();
            // java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
            //  https://my.oschina.net/mengzhang6/blog/2395893
            Object[] arguments = new Object[paramsArray.length];
            for (int i = 0; i < paramsArray.length; i++) {
                if (paramsArray[i] instanceof BindingResult || paramsArray[i] instanceof ServletRequest || paramsArray[i] instanceof ServletResponse || paramsArray[i] instanceof MultipartFile) {
                    continue;
                }
                arguments[i] = paramsArray[i];
            }
            String jsonString = JSONObject.toJSONString(arguments);
            if (StringUtils.isNotBlank(jsonString)) {
                jsonString = jsonString.substring(0, 500);
            }
            return jsonString;
        } else {
            StringBuilder params = new StringBuilder();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse) {
                        continue;
                    }
                    params.append("  ").append(paramNames[i]).append(": ").append(args[i]);
                }
            }
            return params.toString();
        }
    }

    @Override
    public void run(String... args) {
        sysLogService = SpringUtils.getBean(SysLogService.class);
    }
}
