package com.outdoor.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.common.AuthUtil;
import com.outdoor.server.common.R;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.entity.User;
import com.outdoor.server.service.ActivityService;
import com.outdoor.server.service.AdminService;
import com.outdoor.server.service.GuideService;
import com.outdoor.server.vo.BookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "管理后台", description = "运营数据、领队审核、用户与活动管理")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private GuideService guideService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/stats")
    public R<Map<String, Object>> stats(@RequestAttribute("role") Integer role) {
        AuthUtil.requireAdmin(role);
        return R.ok(adminService.stats());
    }

    @GetMapping("/users")
    public R<Page<User>> users(@RequestAttribute("role") Integer role,
                               @RequestParam(defaultValue = "1") Long page,
                               @RequestParam(defaultValue = "10") Long size,
                               @RequestParam(required = false) String keyword) {
        AuthUtil.requireAdmin(role);
        return R.ok(adminService.users(page, size, keyword));
    }

    @PutMapping("/user/status/{id}")
    public R<?> setUserStatus(@RequestAttribute("role") Integer role,
                              @PathVariable Long id,
                              @RequestParam Integer status) {
        AuthUtil.requireAdmin(role);
        adminService.setUserStatus(id, status);
        return R.ok();
    }

    @GetMapping("/guides")
    public R<List<Guide>> guides(@RequestAttribute("role") Integer role,
                                 @RequestParam(required = false) Integer status) {
        AuthUtil.requireAdmin(role);
        return R.ok(guideService.listAll(status));
    }

    @GetMapping("/activities")
    public R<Page<Activity>> activities(@RequestAttribute("role") Integer role,
                                        @RequestParam(defaultValue = "1") Long page,
                                        @RequestParam(defaultValue = "10") Long size,
                                        @RequestParam(required = false) Integer status) {
        AuthUtil.requireAdmin(role);
        return R.ok(adminService.activities(page, size, status));
    }

    @PutMapping("/activity/status/{id}")
    public R<?> setActivityStatus(@RequestAttribute("role") Integer role,
                                  @PathVariable Long id,
                                  @RequestParam Integer status) {
        AuthUtil.requireAdmin(role);
        activityService.updateStatus(id, status);
        return R.ok();
    }

    @GetMapping("/bookings")
    public R<Page<BookingVO>> bookings(@RequestAttribute("role") Integer role,
                                       @RequestParam(defaultValue = "1") Long page,
                                       @RequestParam(defaultValue = "10") Long size,
                                       @RequestParam(required = false) Integer status) {
        AuthUtil.requireAdmin(role);
        return R.ok(adminService.bookings(page, size, status));
    }
}
