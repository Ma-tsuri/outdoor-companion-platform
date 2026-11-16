package com.outdoor.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.dto.ActivityDTO;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "户外活动", description = "活动浏览、详情、发布与维护")
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/list")
    public R<Page<Activity>> list(@RequestParam(defaultValue = "1") Long page,
                                   @RequestParam(defaultValue = "10") Long size,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Long guideId) {
        return R.ok(activityService.listPage(page, size, type, keyword, guideId));
    }

    @GetMapping("/detail/{id}")
    public R<Activity> detail(@PathVariable Long id) {
        return R.ok(activityService.detail(id));
    }

    @GetMapping("/mine")
    public R<java.util.List<Activity>> mine(@RequestAttribute("userId") Long userId) {
        return R.ok(activityService.mine(userId));
    }

    @PostMapping("/create")
    public R<Activity> create(@RequestAttribute("userId") Long userId, @Valid @RequestBody ActivityDTO dto) {
        return R.ok(activityService.create(userId, dto));
    }

    @PutMapping("/update")
    public R<Activity> update(@RequestAttribute("userId") Long userId, @Valid @RequestBody ActivityDTO dto) {
        return R.ok(activityService.update(userId, dto));
    }

    @DeleteMapping("/delete/{id}")
    public R<?> delete(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        activityService.delete(userId, id);
        return R.ok();
    }
}
