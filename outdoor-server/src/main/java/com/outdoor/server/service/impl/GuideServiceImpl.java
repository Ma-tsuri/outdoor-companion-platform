package com.outdoor.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.server.entity.Activity;
import com.outdoor.server.entity.Guide;
import com.outdoor.server.entity.User;
import com.outdoor.server.mapper.ActivityMapper;
import com.outdoor.server.mapper.GuideMapper;
import com.outdoor.server.mapper.UserMapper;
import com.outdoor.server.service.GuideService;
import com.outdoor.server.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GuideServiceImpl extends ServiceImpl<GuideMapper, Guide> implements GuideService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public Guide apply(Long userId, Guide guide) {
        Guide exist = baseMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (exist != null && !Integer.valueOf(2).equals(exist.getStatus())) {
            throw new RuntimeException("您已提交过申请，请耐心等待审核");
        }
        if (exist == null) {
            exist = new Guide();
            exist.setUserId(userId);
        }
        exist.setRealName(guide.getRealName());
        exist.setIdCard(guide.getIdCard());
        exist.setIntro(guide.getIntro());
        exist.setTags(guide.getTags());
        exist.setPrice(guide.getPrice());
        exist.setCity(guide.getCity());
        exist.setStatus(0);
        saveOrUpdate(exist);
        return exist;
    }

    @Override
    public Guide mine(Long userId) {
        Guide guide = baseMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (guide != null) {
            fillGuideInfo(Collections.singletonList(guide));
        }
        return guide;
    }

    @Override
    public Guide updateProfile(Long userId, Guide guide) {
        Guide exist = baseMapper.selectOne(new LambdaQueryWrapper<Guide>().eq(Guide::getUserId, userId));
        if (exist == null) {
            throw new RuntimeException("请先提交领队申请");
        }
        exist.setRealName(guide.getRealName());
        exist.setIntro(guide.getIntro());
        exist.setTags(guide.getTags());
        exist.setPrice(guide.getPrice());
        exist.setCity(guide.getCity());
        updateById(exist);
        return exist;
    }

    @Override
    public List<Guide> listGuides() {
        List<Guide> list = lambdaQuery().eq(Guide::getStatus, 1).orderByDesc(Guide::getCreateTime).list();
        fillGuideInfo(list);
        return list;
    }

    @Override
    public List<Guide> listAll(Integer status) {
        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Guide::getStatus, status);
        }
        wrapper.orderByDesc(Guide::getCreateTime);
        List<Guide> list = baseMapper.selectList(wrapper);
        fillGuideInfo(list);
        return list;
    }

    @Override
    public Guide detail(Long id) {
        Guide guide = getById(id);
        if (guide == null) {
            throw new RuntimeException("领队不存在");
        }
        fillGuideInfo(Collections.singletonList(guide));
        return guide;
    }

    @Override
    public void audit(Long id, Integer status) {
        Guide guide = getById(id);
        if (guide == null) {
            throw new RuntimeException("申请记录不存在");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new RuntimeException("审核状态不正确");
        }
        guide.setStatus(status);
        updateById(guide);
        User user = userMapper.selectById(guide.getUserId());
        if (user != null) {
            user.setRole(status == 1 ? 1 : 0);
            userMapper.updateById(user);
            messageService.send(null, user.getId(),
                    status == 1 ? "恭喜，您的领队资质已通过审核，现在可以发布活动了。"
                            : "很遗憾，您的领队申请未通过审核，请完善资料后重新提交。");
        }
    }

    private void fillGuideInfo(List<Guide> guides) {
        if (guides == null || guides.isEmpty()) {
            return;
        }
        Set<Long> userIds = guides.stream().map(Guide::getUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Guide guide : guides) {
            User user = userMap.get(guide.getUserId());
            if (user != null) {
                guide.setUserName(user.getUsername());
                guide.setAvatar(user.getAvatar());
            }
            Long count = activityMapper.selectCount(new LambdaQueryWrapper<Activity>()
                    .eq(Activity::getGuideId, guide.getId())
                    .eq(Activity::getStatus, 1));
            guide.setActivityCount(count == null ? 0L : count);
            if (!StringUtils.hasText(guide.getRealName()) && user != null) {
                guide.setRealName(user.getUsername());
            }
        }
    }
}
