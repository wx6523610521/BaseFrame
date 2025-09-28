package work.chncyl.base.global.exception;

import work.chncyl.base.global.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

/**
 * 异常处理器
 *
 * @Author scott
 * @Date 2019
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    /**
     * 401无权限异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(work.chncyl.base.global.exception.Global401Exception.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<?> handle401Exception(work.chncyl.base.global.exception.Global401Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.error401(e.getMessage());
    }

    /**
     * 弱密码异常
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(work.chncyl.base.global.exception.WeakPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<?> handleWeakPasswordException(work.chncyl.base.global.exception.WeakPasswordException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error412(e.getMessage());
    }

    /**
     * 路由不存在
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<?> handlerNoFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error404("路径不存在，请检查路径是否正确");
    }

    /**
     * 路由不存在
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(work.chncyl.base.global.exception.NotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<?> handlerNoFoundException(work.chncyl.base.global.exception.NotAcceptableException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error404("路径不存在，请检查路径是否正确");
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error500("数据库中已存在该记录");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({UnauthorizedException.class, AuthorizationException.class})
    public ApiResult<?> handleAuthorizationException(AuthorizationException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error401("没有权限，请联系管理员授权");
    }


    /**
     * @param e
     * @return
     * @Author 政辉
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        StringBuffer sb = new StringBuffer();
        sb.append("不支持");
        sb.append(e.getMethod());
        sb.append("请求方法，");
        sb.append("支持以下");
        String[] methods = e.getSupportedMethods();
        if (methods != null) {
            for (String str : methods) {
                sb.append(str);
                sb.append("、");
            }
        }
        log.error(sb.toString(), e);
        return ApiResult.error405(sb.toString());
    }

    /**
     * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResult<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error500("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResult<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error500("数据库操作错误：" + e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ApiResult<?> handleValidatedException(Exception e) {
        String message = "";
        if (e instanceof MethodArgumentNotValidException) {
            // BeanValidation exception
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            message = ex.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else if (e instanceof ConstraintViolationException) {
            // BeanValidation GET simple param
            ConstraintViolationException ex = (ConstraintViolationException) e;
            message = ex.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
        } else if (e instanceof BindException) {
            // BeanValidation GET object param
            BindException ex = (BindException) e;
            message = ex.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        }
        return ApiResult.error500(message);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.error500("操作失败，" + e.getMessage());
    }
}
