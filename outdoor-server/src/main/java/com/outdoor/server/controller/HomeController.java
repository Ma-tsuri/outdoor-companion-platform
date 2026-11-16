package com.outdoor.server.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index.html"})
    public ResponseEntity<String> index() {
        String html = "<!doctype html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>山野同行 API</title><style>"
                + "*{box-sizing:border-box}body{margin:0;min-height:100vh;display:grid;place-items:center;"
                + "font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#0d2418;color:#edf5ee}"
                + ".card{width:min(660px,calc(100% - 32px));padding:42px;border:1px solid rgba(255,255,255,.14);"
                + "border-radius:24px;background:linear-gradient(145deg,rgba(31,76,49,.96),rgba(16,43,28,.96));"
                + "box-shadow:0 24px 80px rgba(0,0,0,.32)}h1{margin:0 0 12px;font-size:32px}"
                + "p{line-height:1.8;color:#b9cfbf}a{color:#f3c76b;font-weight:700}.links{display:grid;gap:12px;margin-top:24px}"
                + ".link{padding:14px 16px;border-radius:12px;background:rgba(255,255,255,.07);text-decoration:none;color:#edf5ee}"
                + ".link span{float:right;color:#9eb5a4}.tip{margin-top:22px;padding-top:18px;border-top:1px solid rgba(255,255,255,.12);"
                + "font-size:14px;color:#9eb5a4}</style></head><body><main class=\"card\">"
                + "<h1>▲ 山野同行 · 后端服务</h1><p>Spring Boot API 已正常启动。这里是接口服务，不是平台页面。</p>"
                + "<div class=\"links\"><a class=\"link\" href=\"http://localhost:5173\">打开前端平台 <span>localhost:5173 →</span></a>"
                + "<a class=\"link\" href=\"/api/activity/list\">查看活动接口 <span>/api/activity/list →</span></a>"
                + "<a class=\"link\" href=\"/swagger-ui/index.html\">在线接口文档 <span>Swagger UI →</span></a></div>"
                + "<div class=\"tip\">接口前缀：/api &nbsp;·&nbsp; 后端端口：8080 &nbsp;·&nbsp; 前端端口：5173</div>"
                + "</main></body></html>";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
