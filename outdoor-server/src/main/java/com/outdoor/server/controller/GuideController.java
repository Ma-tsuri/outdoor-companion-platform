package com.outdoor.server.controller;

import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.common.AuthUtil;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "认证领队", description = "领队申请、资料维护、公开列表与资质审核")
@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;

    @PostMapping("/apply")
    public R<Guide> apply(@RequestAttribute("userId") Long userId, @RequestBody Guide guide) {
        return R.ok(guideService.apply(userId, guide));
    }

    @GetMapping("/mine")
    public R<Guide> mine(@RequestAttribute("userId") Long userId) {
        return R.ok(guideService.mine(userId));
    }

    @PutMapping("/update")
    public R<Guide> update(@RequestAttribute("userId") Long userId, @RequestBody Guide guide) {
        return R.ok(guideService.updateProfile(userId, guide));
    }

    @GetMapping("/list")
    public R<List<Guide>> list() {
        return R.ok(guideService.listGuides());
    }

    @GetMapping("/detail/{id}")
    public R<Guide> detail(@PathVariable Long id) {
        return R.ok(guideService.detail(id));
    }

    @PutMapping("/audit/{id}")
    public R<?> audit(@RequestAttribute("role") Integer role, @PathVariable Long id, @RequestParam Integer status) {
        AuthUtil.requireAdmin(role);
        guideService.audit(id, status);
        return R.ok();
    }
}
