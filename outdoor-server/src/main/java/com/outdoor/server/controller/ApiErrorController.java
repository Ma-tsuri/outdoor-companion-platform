package com.outdoor.server.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Object codeAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = codeAttr instanceof Integer ? (Integer) codeAttr : 500;
        HttpStatus status = HttpStatus.resolve(code);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", code);
        body.put("message", status == null ? "请求处理失败" : status.getReasonPhrase());
        body.put("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        body.put("tip", "前端入口为 http://localhost:5173，后端接口统一以 /api 开头");
        return ResponseEntity.status(code).body(body);
    }
}
