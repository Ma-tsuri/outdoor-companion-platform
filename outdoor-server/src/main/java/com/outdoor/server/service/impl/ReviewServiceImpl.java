package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.entity.Booking;
import com.outdoor.server.entity.Review;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.BookingMapper;
import com.outdoor.server.mapper.ReviewMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.ReviewService;
import com.outdoor.server.service.MessageService;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.mapper.GuideMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public List<Review> listReviews(Long activityId, Long guideId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(Review::getActivityId, activityId);
        }
        if (guideId != null) {
            wrapper.eq(Review::getGuideId, guideId);
        }
        wrapper.orderByDesc(Review::getCreateTime);
        List<Review> list = baseMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return list;
        }
        Set<Long> userIds = list.stream().map(Review::getUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Review review : list) {
            User user = userMap.get(review.getUserId());
            if (user != null) {
                review.setUserName(user.getUsername());
                review.setUserAvatar(user.getAvatar());
            }
        }
        return list;
    }

    @Override
    public Review create(Long userId, Review review) {
        if (review.getBookingId() == null) {
            throw new RuntimeException("请选择要评价的订单");
        }
        Booking booking = bookingMapper.selectById(review.getBookingId());
        if (booking == null || !booking.getUserId().equals(userId)
                || !Integer.valueOf(3).equals(booking.getStatus())) {
            throw new RuntimeException("订单完成后才能评价");
        }
        Long exists = baseMapper.selectCount(new LambdaQueryWrapper<Review>()
                .eq(Review::getBookingId, review.getBookingId()));
        if (exists != null && exists > 0) {
            throw new RuntimeException("该订单已评价");
        }
        int rating = review.getRating() == null ? 5 : review.getRating();
        review.setId(null);
        review.setRating(Math.max(1, Math.min(5, rating)));
        review.setUserId(userId);
        review.setActivityId(booking.getActivityId());
        review.setGuideId(booking.getGuideId());
        save(review);
        Guide guide = guideMapper.selectById(booking.getGuideId());
        if (guide != null) {
            messageService.send(userId, guide.getUserId(),
                    "您收到一条 " + review.getRating() + " 星评价：" + review.getContent());
        }
        return review;
    }
}
