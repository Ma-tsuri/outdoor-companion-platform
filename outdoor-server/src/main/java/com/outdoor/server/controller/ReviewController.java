package com.outdoor.server.controller;

import com.outdoor.server.common.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.outdoor.server.entity.Review;
import com.outdoor.server.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "活动评价", description = "评价列表与行程评价提交")
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/list")
    public R<List<Review>> list(@RequestParam(required = false) Long activityId,
                                 @RequestParam(required = false) Long guideId) {
        return R.ok(reviewService.listReviews(activityId, guideId));
    }

    @PostMapping("/create")
    public R<Review> create(@RequestAttribute("userId") Long userId, @RequestBody Review review) {
        return R.ok(reviewService.create(userId, review));
    }
}
