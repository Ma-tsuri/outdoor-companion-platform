package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.User;
import com.outdoor.server.vo.BookingVO;

import java.util.Map;

public interface AdminService {
    Map<String, Object> stats();
    Page<User> users(Long page, Long size, String keyword);
    void setUserStatus(Long id, Integer status);
    Page<Activity> activities(Long page, Long size, Integer status);
    Page<BookingVO> bookings(Long page, Long size, Integer status);
}
