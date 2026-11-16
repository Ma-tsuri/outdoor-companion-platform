package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.dto.ActivityDTO;
import com.outdoor.server.entity.Activity;

import java.util.List;

public interface ActivityService extends IService<Activity> {
    Page<Activity> listPage(Long page, Long size, String type, String keyword, Long guideId);
    Activity detail(Long id);
    List<Activity> mine(Long userId);
    Page<Activity> pageAll(Long page, Long size, Integer status);
    void updateStatus(Long id, Integer status);
    Activity create(Long userId, ActivityDTO dto);
    Activity update(Long userId, ActivityDTO dto);
    void delete(Long userId, Long id);
}
