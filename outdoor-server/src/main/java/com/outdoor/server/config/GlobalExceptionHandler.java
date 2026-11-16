package com.outdoor.server.config;

import com.outdoor.server.common.R;
import org.springframework.validation.BindException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntime(RuntimeException e) {
        return R.error(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<?> handleValid(Exception e) {
        String msg = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            msg = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage();
        }
        return R.error(msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> handleBody(HttpMessageNotReadableException e) {
        return R.error(400, "请求数据格式不正确");
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public R<?> handleParameter(Exception e) {
        return R.error(400, "请求参数不正确");
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        return R.error("系统繁忙：" + e.getMessage());
    }
}
