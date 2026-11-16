package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.dto.ActivityDTO;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.entity.Review;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.ActivityMapper;
import com.outdoor.server.mapper.GuideMapper;
import com.outdoor.server.mapper.ReviewMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Page<Activity> listPage(Long page, Long size, String type, String keyword, Long guideId) {
        Page<Activity> p = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1);
        if (StringUtils.hasText(type)) {
            wrapper.eq(Activity::getType, type);
        }
        if (guideId != null) {
            wrapper.eq(Activity::getGuideId, guideId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Activity::getTitle, keyword).or().like(Activity::getDestination, keyword));
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> result = baseMapper.selectPage(p, wrapper);
        fillGuideInfo(result.getRecords());
        return result;
    }

    @Override
    public Activity detail(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        fillGuideInfo(Collections.singletonList(activity));
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>().eq(Review::getActivityId, id));
        activity.setReviewCount((long) reviews.size());
        activity.setAvgRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0));
        return activity;
    }

    @Override
    public List<Activity> mine(Long userId) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (guide == null) {
            return Collections.emptyList();
        }
        List<Activity> list = lambdaQuery()
                .eq(Activity::getGuideId, guide.getId())
                .orderByDesc(Activity::getCreateTime)
                .list();
        fillGuideInfo(list);
        return list;
    }

    @Override
    public Page<Activity> pageAll(Long page, Long size, Integer status) {
        Page<Activity> p = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> result = baseMapper.selectPage(p, wrapper);
        fillGuideInfo(result.getRecords());
        return result;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setStatus(status);
        updateById(activity);
    }

    @Override
    public Activity create(Long userId, ActivityDTO dto) {
        User user = userMapper.selectById(userId);
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (user == null || !Integer.valueOf(1).equals(user.getRole())
                || guide == null || !Integer.valueOf(1).equals(guide.getStatus())) {
            throw new RuntimeException("暂无权限发布活动");
        }
        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);
        activity.setGuideId(guide.getId());
        activity.setCurrentNum(0);
        activity.setStatus(1);
        save(activity);
        return activity;
    }

    @Override
    public Activity update(Long userId, ActivityDTO dto) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        Activity activity = getById(dto.getId());
        if (guide == null || activity == null || !activity.getGuideId().equals(guide.getId())) {
            throw new RuntimeException("无权修改该活动");
        }
        BeanUtils.copyProperties(dto, activity, "id", "guideId", "currentNum", "status", "createTime", "updateTime", "deleted");
        updateById(activity);
        return activity;
    }

    @Override
    public void delete(Long userId, Long id) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        Activity activity = getById(id);
        if (guide == null || activity == null || !activity.getGuideId().equals(guide.getId())) {
            throw new RuntimeException("无权删除该活动");
        }
        removeById(id);
    }

    private void fillGuideInfo(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return;
        }
        Set<Long> guideIds = activities.stream().map(Activity::getGuideId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (guideIds.isEmpty()) {
            return;
        }
        Map<Long, Guide> guideMap = guideMapper.selectBatchIds(guideIds).stream()
                .collect(Collectors.toMap(Guide::getId, Function.identity()));
        Set<Long> userIds = guideMap.values().stream().map(Guide::getUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Long> activityCountMap = new HashMap<>();
        for (Long guideId : guideIds) {
            activityCountMap.put(guideId, baseMapper.selectCount(
                    new LambdaQueryWrapper<Activity>()
                            .eq(Activity::getGuideId, guideId)
                            .eq(Activity::getStatus, 1)));
        }
        for (Activity activity : activities) {
            Guide guide = guideMap.get(activity.getGuideId());
            if (guide == null) {
                continue;
            }
            User user = userMap.get(guide.getUserId());
            activity.setGuideName(StringUtils.hasText(guide.getRealName())
                    ? guide.getRealName() : (user == null ? "户外领队" : user.getUsername()));
            activity.setGuideCity(guide.getCity());
            activity.setGuideTags(guide.getTags());
            activity.setGuideIntro(guide.getIntro());
            activity.setGuidePrice(guide.getPrice());
            activity.setGuideAvatar(user == null ? null : user.getAvatar());
            activity.setGuideActivityCount(activityCountMap.get(activity.getGuideId()));
        }
    }
}
