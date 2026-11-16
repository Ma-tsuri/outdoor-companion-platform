package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.Booking;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.ActivityMapper;
import com.outdoor.server.mapper.BookingMapper;
import com.outdoor.server.mapper.GuideMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.ActivityService;
import com.outdoor.server.service.AdminService;
import com.outdoor.server.service.BookingService;
import com.outdoor.server.vo.BookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private BookingService bookingService;

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userCount", userMapper.selectCount(null));
        map.put("guideCount", guideMapper.selectCount(
                new LambdaQueryWrapper<Guide>().eq(Guide::getStatus, 1)));
        map.put("pendingGuideCount", guideMapper.selectCount(
                new LambdaQueryWrapper<Guide>().eq(Guide::getStatus, 0)));
        map.put("activityCount", activityMapper.selectCount(
                new LambdaQueryWrapper<Activity>().eq(Activity::getStatus, 1)));
        map.put("bookingCount", bookingMapper.selectCount(null));
        map.put("pendingBookingCount", bookingMapper.selectCount(
                new LambdaQueryWrapper<Booking>().eq(Booking::getStatus, 0)));
        map.put("completedBookingCount", bookingMapper.selectCount(
                new LambdaQueryWrapper<Booking>().eq(Booking::getStatus, 3)));
        List<Booking> paid = bookingMapper.selectList(new LambdaQueryWrapper<Booking>()
                .in(Booking::getStatus, 1, 3));
        BigDecimal revenue = paid.stream().map(Booking::getTotalPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("totalRevenue", revenue);
        map.put("bookingStatusDistribution", bookingStatusDistribution());
        map.put("activityTypeDistribution", activityTypeDistribution());
        return map;
    }

    private List<Map<String, Object>> bookingStatusDistribution() {
        List<Booking> all = bookingMapper.selectList(null);
        String[] labels = {"待支付", "已支付", "已取消", "已完成"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int status = 0; status <= 3; status++) {
            final int current = status;
            long count = all.stream().filter(item -> Integer.valueOf(current).equals(item.getStatus())).count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", labels[status]);
            item.put("value", count);
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> activityTypeDistribution() {
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>().eq(Activity::getStatus, 1));
        return activities.stream()
                .filter(item -> StringUtils.hasText(item.getType()))
                .collect(Collectors.groupingBy(Activity::getType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", entry.getKey());
                    item.put("value", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<User> users(Long page, Long size, String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(p, wrapper);
        result.getRecords().forEach(user -> user.setPassword(null));
        return result;
    }

    @Override
    public void setUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("状态不正确");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public Page<Activity> activities(Long page, Long size, Integer status) {
        return activityService.pageAll(page, size, status);
    }

    @Override
    public Page<BookingVO> bookings(Long page, Long size, Integer status) {
        return bookingService.pageAll(page, size, status);
    }
}
