package com.outdoor.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.server.entity.Review;

import java.util.List;

public interface ReviewService extends IService<Review> {
    List<Review> listReviews(Long activityId, Long guideId);
    Review create(Long userId, Review review);
}
