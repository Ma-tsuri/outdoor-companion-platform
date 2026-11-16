package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.entity.Guide;

import java.util.List;

public interface GuideService extends IService<Guide> {
    Guide apply(Long userId, Guide guide);
    Guide mine(Long userId);
    Guide updateProfile(Long userId, Guide guide);
    List<Guide> listGuides();
    List<Guide> listAll(Integer status);
    Guide detail(Long id);
    void audit(Long id, Integer status);
}
