package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.dto.BookingDTO;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.Booking;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.entity.Review;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.ActivityMapper;
import com.outdoor.server.mapper.BookingMapper;
import com.outdoor.server.mapper.GuideMapper;
import com.outdoor.server.mapper.ReviewMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.BookingService;
import com.outdoor.server.service.MessageService;
import com.outdoor.server.vo.BookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl extends ServiceImpl<BookingMapper, Booking> implements BookingService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Booking create(Long userId, BookingDTO dto) {
        if (dto.getActivityId() == null) {
            throw new RuntimeException("请选择活动");
        }
        int num = dto.getNum() == null ? 1 : dto.getNum();
        if (num <= 0) {
            throw new RuntimeException("预约人数必须大于 0");
        }
        Activity activity = activityMapper.selectById(dto.getActivityId());
        if (activity == null || !Integer.valueOf(1).equals(activity.getStatus())) {
            throw new RuntimeException("活动不存在或已下架");
        }
        if (activity.getStartTime() != null && activity.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("活动已开始，无法预约");
        }
        LocalDate bookDate = dto.getBookDate() == null
                ? (activity.getStartTime() == null ? LocalDate.now() : activity.getStartTime().toLocalDate())
                : dto.getBookDate();
        if (bookDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("预约日期不能早于今天");
        }

        int updated = activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                .setSql("current_num = current_num + " + num)
                .eq(Activity::getId, activity.getId())
                .eq(Activity::getStatus, 1)
                .apply("current_num + {0} <= max_num", num));
        if (updated == 0) {
            throw new RuntimeException("活动名额不足");
        }

        Booking booking = new Booking();
        booking.setActivityId(activity.getId());
        booking.setUserId(userId);
        booking.setGuideId(activity.getGuideId());
        booking.setBookDate(bookDate);
        booking.setNum(num);
        BigDecimal price = activity.getPrice() == null ? BigDecimal.ZERO : activity.getPrice();
        booking.setTotalPrice(price.multiply(new BigDecimal(num)));
        booking.setStatus(0);
        try {
            save(booking);
        } catch (RuntimeException e) {
            activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                    .setSql("current_num = greatest(current_num - " + num + ", 0)")
                    .eq(Activity::getId, activity.getId()));
            throw e;
        }
        Guide guide = guideMapper.selectById(activity.getGuideId());
        if (guide != null) {
            messageService.send(userId, guide.getUserId(),
                    "您收到新的预约订单：" + activity.getTitle() + "，" + num + " 人，等待用户支付。");
        }
        return booking;
    }

    @Override
    public List<BookingVO> listByUser(Long userId) {
        return toVOList(lambdaQuery().eq(Booking::getUserId, userId)
                .orderByDesc(Booking::getCreateTime).list());
    }

    @Override
    public List<BookingVO> listByGuide(Long userId) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (guide == null) {
            return Collections.emptyList();
        }
        return toVOList(lambdaQuery().eq(Booking::getGuideId, guide.getId())
                .orderByDesc(Booking::getCreateTime).list());
    }

    @Override
    public Page<BookingVO> pageAll(Long page, Long size, Integer status) {
        Page<Booking> p = new Page<>(page, size);
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Booking::getStatus, status);
        }
        wrapper.orderByDesc(Booking::getCreateTime);
        Page<Booking> result = baseMapper.selectPage(p, wrapper);
        Page<BookingVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(toVOList(result.getRecords()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long id) {
        Booking booking = getById(id);
        if (booking == null || !booking.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!Integer.valueOf(0).equals(booking.getStatus()) && !Integer.valueOf(1).equals(booking.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }
        activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
                .setSql("current_num = greatest(current_num - " + booking.getNum() + ", 0)")
                .eq(Activity::getId, booking.getActivityId()));
        booking.setStatus(2);
        updateById(booking);
        Guide guide = guideMapper.selectById(booking.getGuideId());
        if (guide != null) {
            messageService.send(userId, guide.getUserId(),
                    "用户取消了订单 #" + booking.getId() + "。");
        }
    }

    @Override
    public void pay(Long userId, Long id) {
        Booking booking = getById(id);
        if (booking == null || !booking.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!Integer.valueOf(0).equals(booking.getStatus())) {
            throw new RuntimeException("订单状态异常，无法支付");
        }
        booking.setStatus(1);
        booking.setPayTime(LocalDateTime.now());
        updateById(booking);
        Guide guide = guideMapper.selectById(booking.getGuideId());
        if (guide != null) {
            messageService.send(userId, guide.getUserId(),
                    "订单 #" + booking.getId() + " 已支付，请按约定提供服务。");
        }
    }

    @Override
    public void complete(Long userId, Long id) {
        Guide guide = guideMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        Booking booking = getById(id);
        if (guide == null || booking == null || !guide.getId().equals(booking.getGuideId())) {
            throw new RuntimeException("无权操作该订单");
        }
        if (!Integer.valueOf(1).equals(booking.getStatus())) {
            throw new RuntimeException("仅已支付订单可确认完成");
        }
        booking.setStatus(3);
        updateById(booking);
        messageService.send(userId, booking.getUserId(),
                "订单 #" + booking.getId() + " 已完成，欢迎评价本次户外体验。");
    }

    private List<BookingVO> toVOList(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> activityIds = bookings.stream().map(Booking::getActivityId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<Long> guideIds = bookings.stream().map(Booking::getGuideId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds = bookings.stream().map(Booking::getUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Activity> activityMap = activityIds.isEmpty() ? Collections.emptyMap()
                : activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(Activity::getId, Function.identity()));
        Map<Long, Guide> guideMap = guideIds.isEmpty() ? Collections.emptyMap()
                : guideMapper.selectBatchIds(guideIds).stream()
                .collect(Collectors.toMap(Guide::getId, Function.identity()));
        Set<Long> allUserIds = new java.util.HashSet<>(userIds);
        guideMap.values().forEach(g -> {
            if (g.getUserId() != null) {
                allUserIds.add(g.getUserId());
            }
        });
        Map<Long, User> userMap = allUserIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Set<Long> bookingIds = bookings.stream().map(Booking::getId).collect(Collectors.toSet());
        Set<Long> reviewedIds = reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                        .in(Review::getBookingId, bookingIds))
                .stream().map(Review::getBookingId).collect(Collectors.toSet());

        return bookings.stream().map(booking -> {
            BookingVO vo = new BookingVO();
            vo.setId(booking.getId());
            vo.setActivityId(booking.getActivityId());
            vo.setUserId(booking.getUserId());
            vo.setGuideId(booking.getGuideId());
            vo.setBookDate(booking.getBookDate());
            vo.setNum(booking.getNum());
            vo.setTotalPrice(booking.getTotalPrice());
            vo.setStatus(booking.getStatus());
            vo.setPayTime(booking.getPayTime());
            vo.setCreateTime(booking.getCreateTime());
            vo.setReviewed(reviewedIds.contains(booking.getId()));
            Activity activity = activityMap.get(booking.getActivityId());
            if (activity != null) {
                vo.setActivityTitle(activity.getTitle());
                vo.setActivityCover(activity.getCover());
            }
            Guide guide = guideMap.get(booking.getGuideId());
            if (guide != null) {
                User guideUser = userMap.get(guide.getUserId());
                vo.setGuideName(StringUtils.hasText(guide.getRealName()) ? guide.getRealName()
                        : (guideUser == null ? "户外领队" : guideUser.getUsername()));
            }
            User user = userMap.get(booking.getUserId());
            vo.setUserName(user == null ? "用户" : user.getUsername());
            return vo;
        }).collect(Collectors.toList());
    }
}
